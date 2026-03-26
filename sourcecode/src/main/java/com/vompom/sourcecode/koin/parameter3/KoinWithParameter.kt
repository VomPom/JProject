package com.vompom.sourcecode.koin.parameter3


import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 *
 * Created by @juliswang on 2025/08/25 15:26
 *
 * @Description 基于 Koin 的设计，实现其核心模块(不包含 Scope)：
 *
 *     能够实现实例动态传递参数
 *
 *     startKoin {
 *          modules(
 *             module {
 *                 single { (data: Int) -> ComponentInt(data) }
 *                 factory { (data1: Int, data2: Float) -> ComponentIntFloat(data1, data2) }
 *             }
 *         )
 *      }
 */
@DslMarker
annotation class KoinApplicationDslMarker

@DslMarker
annotation class KoinDslMarker

@DslMarker
annotation class OptionDslMarker


fun startKoin(appDeclaration: KoinApplication.() -> Unit): KoinApplication = GlobalContext.startKoin(appDeclaration)

object GlobalContext {
    private var _koin: Koin? = null

    fun get(): Koin = _koin ?: error("KoinApplication has not been started")

    fun startKoin(appDeclaration: KoinApplication.() -> Unit): KoinApplication {
        val koinApplication = KoinApplication.init()
        register(koinApplication)
        appDeclaration(koinApplication)
        return koinApplication
    }

    private fun register(koinApplication: KoinApplication) {
        if (_koin != null) {
            throw RuntimeException("A Koin Application has already been started")
        }
        _koin = koinApplication.koin
    }
}

@KoinApplicationDslMarker
class KoinApplication {
    val koin = Koin()

    @KoinDslMarker
    fun modules(module: Module): KoinApplication = modules(arrayListOf<Module>(module))

    @KoinDslMarker
    fun modules(module: List<Module>): KoinApplication {
        koin.loadModels(module)
        return this
    }

    companion object {
        fun init(): KoinApplication =
            KoinApplication()
    }
}

fun koinApplication(declare: KoinApplication.() -> Unit): KoinApplication {
    val koinApplication = KoinApplication.init()
    declare.invoke(koinApplication)
    return koinApplication
}

class Koin() {
    val resolver = CoreResolver(this)

    val instanceRegistry = InstanceRegistry(this)
    val scopeRegistry = ScopeRegistry(this)
    fun loadModels(module: List<Module>) {
        instanceRegistry.loadModels(module)
    }

    inline fun <reified T> get(
        qualifier: String? = null,
        noinline parameters: ParametersDefinition? = null,
    ): T = scopeRegistry.rootScope.get(qualifier, parameters)

    fun createScope(
        scopeId: String,
        qualifier: String,
        type: KClass<*>? = null,   //scopeArchetype: TypeQualifier? = null
    ): Scope {
        return scopeRegistry.createScope(scopeId, qualifier, type)
    }
}


//////////////////////////////////   Registry Start //////////////////////////////////
class ScopeRegistry(private val _koin: Koin) {
    private val ROOT_SCOPE_ID = "_root_"
    val rootScope = Scope(ROOT_SCOPE_ID, ROOT_SCOPE_ID, isRoot = true, _koin = _koin)
    private val _scopes = ConcurrentHashMap<String, Scope>()
    private val _scopeDefinitions = HashSet<String>()

    init {
        _scopeDefinitions.add(rootScope.scopeQualifier)
        _scopes[rootScope.id] = rootScope
    }

    fun createScope(scopeId: String, qualifier: String, scopeArchetype: KClass<*>? = null): Scope {
        val scope = Scope(qualifier, scopeId, isRoot = false, _koin = _koin)
        _scopeDefinitions.add(qualifier)
        _scopes[scopeId] = scope
        return scope
    }
}

class InstanceRegistry(val _koin: Koin) {
    private val _instances = ConcurrentHashMap<String, InstanceFactory<*>>()

    fun loadModels(modules: List<Module>) {
        modules.forEach { module ->
            module.mappings.forEach { (mapping, factory) ->
                _instances[mapping] = factory
            }
        }
    }

    fun <T> resolveInstance(
        ctx: ResolutionContext
    ): T = resolveDefinition(ctx.clazz, ctx.qualifier, ctx.scope.scopeQualifier)?.get(ctx) as T

    internal fun resolveDefinition(
        clazz: KClass<*>,
        qualifier: String?,
        scopeQualifier: String
    ): InstanceFactory<*>? {
        val indexKey = indexKey(clazz, qualifier, scopeQualifier)
        return _instances[indexKey]
    }
}
////////////////////////////////// Registry END //////////////////////////////////


////////////////////////////////// Module Start //////////////////////////////////
@KoinDslMarker
class Module() {
    val mappings = LinkedHashMap<String, InstanceFactory<*>>()
    val scopes = LinkedHashSet<String>()

    inline fun <reified T> single(
        qualifier: String? = null,
        noinline definition: Definition<T>
    ): KoinDefinition<T> {
        val factory = _singleInstanceFactory<T>(qualifier, definition)
        indexPrimaryType(factory)
        return KoinDefinition(this, factory)
    }

    inline fun <reified T> factory(
        qualifier: String? = null,
        scopeQualifier: String = rootScopeQualifier,
        noinline definition: Definition<T>,
    ): KoinDefinition<T> {
        val factory = _factoryInstanceFactory<T>(qualifier, definition, scopeQualifier)
        indexPrimaryType(factory)
        return KoinDefinition(this, factory)
    }

    inline fun scope(qualifier: String, scopeSet: ScopeDSL.() -> Unit) {
        ScopeDSL(qualifier, this).apply(scopeSet)
        scopes.add(qualifier)
    }

    inline fun <reified T> scope(scopeSet: ScopeDSL.() -> Unit) {
        val qualifier = T::class.java.name
        ScopeDSL(qualifier, this).apply(scopeSet)
        scopes.add(qualifier)
    }

    fun indexPrimaryType(instanceFactory: InstanceFactory<*>) {
        val def = instanceFactory.beanDefinition
        val mapping = indexKey(def.primaryType, def.qualifier, def.scopeQualifier)
        saveMapping(mapping, instanceFactory)
    }

    internal fun saveMapping(mapping: String, factory: InstanceFactory<*>) {
        mappings[mapping] = factory
    }

}

data class KoinDefinition<R>(val module: Module, val factory: InstanceFactory<R>)

inline fun <reified T> _createDefinition(
    qualifier: String? = null,
    scopeQualifier: String = rootScopeQualifier,
    noinline definition: Definition<T>,
): BeanDefinition<T> {
    return BeanDefinition(T::class, qualifier, scopeQualifier, definition)
}

fun module(declare: Module.() -> Unit): Module {
    val module = Module()
    declare(module)
    return module
}

inline fun indexKey(clazz: KClass<*>, typeQualifier: String?, scopeQualifier: String): String {
    return buildString {
        append(clazz.java.name)
        append(':')
        append(typeQualifier ?: "")
        append(':')
        append(scopeQualifier)
    }
}


typealias Definition<T> = Scope.(ParametersHolder) -> T

class BeanDefinition<T>(
    val primaryType: KClass<*>,
    var qualifier: String? = null,
    val scopeQualifier: String,
    val definition: Definition<T>,
)
//////////////////////////////////   Module END //////////////////////////////////


//////////////////////////////////   Factory Start //////////////////////////////////
abstract class InstanceFactory<T>(val beanDefinition: BeanDefinition<T>) {
    abstract fun get(context: ResolutionContext): T

    open fun create(context: ResolutionContext): T {
        val parameters: ParametersHolder = context.parameters ?: ParametersHolder()
        return beanDefinition.definition.invoke(context.scope, parameters)
    }
}

class SingleFactory<T>(beanDefinition: BeanDefinition<T>) : InstanceFactory<T>(beanDefinition) {
    private var value: T? = null
    private fun getValue(): T = value ?: error("Single instance created couldn't return value")
    override fun get(context: ResolutionContext): T {
        if (value == null) {
            value = super.create(context)
        }
        return getValue()
    }
}

class FactoryFactory<T>(beanDefinition: BeanDefinition<T>) : InstanceFactory<T>(beanDefinition) {
    override fun get(context: ResolutionContext): T = super.create(context)
}

class ScopeFactory<T>(beanDefinition: BeanDefinition<T>) : InstanceFactory<T>(beanDefinition) {
    private var values = hashMapOf<String, T>()

    override fun get(context: ResolutionContext): T {
        if (context.scope.scopeQualifier != beanDefinition.scopeQualifier && context.scope.scopeQualifier != beanDefinition.scopeQualifier) {
            error("Wrong Scope qualifier: trying to open instance for ${context.scope.id} in $beanDefinition")
        }
        values[context.scope.id] = create(context)
        return values[context.scope.id]
            ?: error("Factory.get -Scoped instance not found for ${context.scope.id} in $beanDefinition")
    }

    override fun create(context: ResolutionContext): T {
        return if (values[context.scope.id] == null) {
            super.create(context)
        } else {
            values[context.scope.id] ?: error("Scope instance created couldn't return value")
        }
    }
}
//////////////////////////////////   Factory END //////////////////////////////////

@KoinDslMarker
class Scope(
    val scopeQualifier: String,
    val id: String,
    val isRoot: Boolean = false,
    val type: KClass<*>? = null,        //    val scopeArchetype : TypeQualifier? = null,
    val _koin: Koin,
) {
    inline fun <reified T> get(
        qualifier: String? = null,
        noinline parameters: ParametersDefinition? = null,
    ): T {
        return resolve(qualifier, parameters?.invoke())
    }

    inline fun <reified T> resolve(
        qualifier: String?,
        parameters: ParametersHolder? = null,
    ): T {
        val instanceContext = ResolutionContext(this, T::class, qualifier, parameters)
        return _koin.resolver.resolveFromContext(instanceContext)
    }
}

@KoinDslMarker
class ScopeDSL(val scopeQualifier: String, val module: Module) {

    inline fun <reified T> scoped(
        qualifier: String? = null,
        noinline definition: Definition<T>,
    ): KoinDefinition<T> {
        val def = _scopedInstanceFactory(qualifier, definition, scopeQualifier)
        module.indexPrimaryType(def)
        return KoinDefinition(module, def)
    }

    inline fun <reified T> factory(
        qualifier: String? = null,
        noinline definition: Definition<T>,
    ): KoinDefinition<T> {
        return module.factory(qualifier, scopeQualifier, definition)
    }
}


class CoreResolver(private val _koin: Koin) {
    fun <T> resolveFromContext(ctx: ResolutionContext): T {
        return _koin.instanceRegistry.resolveInstance(ctx)
            ?: resolveFromParentScopes(ctx.scope, ctx)
            ?: throw RuntimeException("No definition found for type '${ctx.clazz.java.name}'${ctx.qualifier}.")
    }

    private fun <T> resolveFromParentScopes(scope: Scope, ctx: ResolutionContext): T? {
        if (scope.isRoot) return null
        return findInOtherScope(scope, ctx)
    }

    private fun <T> findInOtherScope(scope: Scope, ctx: ResolutionContext): T? {
        return if (!scope.isRoot) {
            val newContext = ResolutionContext(_koin.scopeRegistry.rootScope, ctx.clazz, ctx.qualifier)
            resolveFromContext(newContext)
        } else null
    }
}

class ResolutionContext(
    val scope: Scope,
    val clazz: KClass<*>,
    val qualifier: String? = null,
    val parameters: ParametersHolder? = null,
)


//////////////////////////////////  Create Factory Start //////////////////////////////////
const val rootScopeQualifier = "_root_"

inline fun <reified T> _singleInstanceFactory(
    qualifier: String? = null,
    noinline definition: Definition<T>,
    scopeQualifier: String = rootScopeQualifier,
): InstanceFactory<T> = SingleFactory(_createDefinition(qualifier, scopeQualifier, definition))

inline fun <reified T> _factoryInstanceFactory(
    qualifier: String? = null,
    noinline definition: Definition<T>,
    scopeQualifier: String = rootScopeQualifier,
): InstanceFactory<T> = FactoryFactory(_createDefinition(qualifier, scopeQualifier, definition))

inline fun <reified T> _scopedInstanceFactory(
    qualifier: String? = null,
    noinline definition: Definition<T>,
    scopeQualifier: String = rootScopeQualifier,
): InstanceFactory<T> = ScopeFactory(_createDefinition(qualifier, scopeQualifier, definition))
////////////////////////////////// Create Factory END //////////////////////////////////


////////////////////////////////// Parameter  Start //////////////////////////////////
@KoinDslMarker
class ParametersHolder(val _values: MutableList<Any?> = mutableListOf()) {

    fun <T> elementAt(i: Int, clazz: KClass<*>): T =
        if (i < _values.size) {
            _values[i] as T
        } else {
            throw Exception(
                "Can't get injected parameter #$i from $this for type '${clazz.java.name}'",
            )
        }

    inline operator fun <reified T> component1(): T = elementAt(0, T::class)
    inline operator fun <reified T> component2(): T = elementAt(1, T::class)
    inline operator fun <reified T> component3(): T = elementAt(2, T::class)
    inline operator fun <reified T> component4(): T = elementAt(3, T::class)
    inline operator fun <reified T> component5(): T = elementAt(4, T::class)

    /**
     * get element at given index
     * return T
     */
    operator fun <T> get(i: Int) = _values[i] as T

    fun <T> set(i: Int, t: T) {
        _values[i] = t as Any
    }
}

fun parametersOf(vararg parameters: Any?) = ParametersHolder(parameters.toMutableList())

typealias ParametersDefinition = () -> ParametersHolder
//////////////////////////////////  Parameter END //////////////////////////////////