package com.vompom.sourcecode.koin.noScope1

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 *
 * Created by @juliswang on 2025/08/20 11:26
 *
 * @Description 基于 Koin 的设计，实现其核心模块(不包含 Scope)：
 *
 *   - 注册实例
 *   - 获取数据
 *
 *     实现下面的注册并能成功获取到单例的值
 *     startKoin {
 *      modules(
 *          module {
 *              single { ComponentA() }
 *              factory { ComponentB(get()) }
 *          }
 *      )
 *     }
 *
 *     val koin = GlobalContext.get()
 *     val componentA1 = koin.get<ComponentA>()
 */
fun startKoin(appDeclaration: KoinApplication.() -> Unit): KoinApplication =
    GlobalContext.startKoin(appDeclaration)

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

class KoinApplication {
    val koin = Koin()

    fun modules(module: Module): KoinApplication =
        modules(arrayListOf<Module>(module))

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
    val instanceRegistry = InstanceRegistry(this)
    fun loadModels(module: List<Module>) {
        instanceRegistry.loadModels(module)
    }

    inline fun <reified T> get(
        qualifier: String? = null,
    ): T = instanceRegistry.resolveInstance(qualifier, T::class)
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
        qualifier: String?,
        clazz: KClass<*>
    ): T = resolveDefinition(clazz, qualifier)?.get() as T

    internal fun resolveDefinition(
        clazz: KClass<*>,
        qualifier: String?,
    ): InstanceFactory<*>? {
        val indexKey = indexKey(clazz, qualifier)
        return _instances[indexKey]
    }
}

class Module() {
    val mappings = LinkedHashMap<String, InstanceFactory<*>>()

    inline fun <reified T> single(qualifier: String? = null, noinline definition: Definition<T>): KoinDefinition<T> {
        val factory = createSingleFactory<T>(qualifier, definition)
        indexPrimaryType(factory)
        return KoinDefinition(this, factory)
    }

    inline fun <reified T> factory(qualifier: String? = null, noinline definition: Definition<T>): KoinDefinition<T> {
        val factory = createFactoryFactory<T>(qualifier, definition)
        indexPrimaryType(factory)
        return KoinDefinition(this, factory)
    }

    fun indexPrimaryType(instanceFactory: InstanceFactory<*>) {
        val def = instanceFactory.beanDefinition
        val mapping = indexKey(def.primaryType, def.qualifier)
        saveMapping(mapping, instanceFactory)
    }

    inline fun <reified T> createSingleFactory(
        qualifier: String? = null,
        noinline definition: Definition<T>
    ): InstanceFactory<T> = SingleFactory(_createDefinition(qualifier, definition))

    inline fun <reified T> createFactoryFactory(
        qualifier: String? = null,
        noinline definition: Definition<T>
    ): InstanceFactory<T> = FactoryFactory(_createDefinition(qualifier, definition))

    internal fun saveMapping(mapping: String, factory: InstanceFactory<*>) {
        mappings[mapping] = factory
    }

    fun <T> get(): T {
        // Not implemented at here...
        return "" as T
    }
}

inline fun <reified T> _createDefinition(
    qualifier: String? = null,
    noinline definition: Definition<T>,
): BeanDefinition<T> {
    return BeanDefinition(T::class, qualifier, definition)
}

fun module(declare: Module.() -> Unit): Module {
    val module = Module()
    declare(module)
    return module
}

inline fun indexKey(clazz: KClass<*>, typeQualifier: String?): String {
    return buildString {
        append(clazz.java.name)
        append(':')
        append(typeQualifier ?: "")
        append(':')
    }
}

//typealias Definition<T> = Scope.() -> T
typealias Definition<T> = () -> T   // !! type is () -> T not Scope.() -> T

class BeanDefinition<T>(
    val primaryType: KClass<*>,
    var qualifier: String? = null,
    val definition: Definition<T>,
)

abstract class InstanceFactory<T>(val beanDefinition: BeanDefinition<T>) {
    abstract fun get(): T

    open fun create(): T = beanDefinition.definition.invoke()
}

class SingleFactory<T>(beanDefinition: BeanDefinition<T>) : InstanceFactory<T>(beanDefinition) {
    private var value: T? = null
    private fun getValue(): T = value ?: error("Single instance created couldn't return value")
    override fun get(): T {
        if (value == null) {
            value = super.create()
        }
        return getValue()
    }
}

class FactoryFactory<T>(beanDefinition: BeanDefinition<T>) : InstanceFactory<T>(beanDefinition) {
    override fun get(): T = super.create()
}

data class KoinDefinition<R>(val module: Module, val factory: InstanceFactory<R>)
