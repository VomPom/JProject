import kotlin.reflect.KClass

// --- DSL 定义部分 ---

// 上下文类
class MyKoinAppDeclaration {
    fun modules(vararg modules: MyModule) {
        println("加载了 ${modules.size} 个模块")
    }
}

class MyModule {
    // 使用 KClass 来引用类型，更接近 Koin 的做法
    fun scope(scopeQualifier: KClass<*>, block: MyScope.() -> Unit) {
        println("在 Module 中定义了 ${scopeQualifier.simpleName} 的 scope")
        MyScope().apply(block)
    }

    fun singleOf(constructor: () -> Any) {
        println("定义了一个 single: ${constructor.javaClass.simpleName}")
    }
}

class MyScope {
    fun scopedOf(constructor: () -> Any) {
        println("定义了一个 scoped: ${constructor.javaClass.simpleName}")
    }
}

// 入口函数 (高阶函数 + 带接收者的 Lambda)
fun startKoin(block: MyKoinAppDeclaration.() -> Unit) {
    println("--- DSL 启动 ---")
    MyKoinAppDeclaration().apply(block)
    println("--- DSL 配置完成 ---")
}

fun module(block: MyModule.() -> Unit): MyModule {
    return MyModule().apply(block)
}


// --- 业务代码，用于测试 ---
class A
class B
class SubClass

// --- 执行入口 ---
fun main() {
    startKoin {
        modules(
            module {
                singleOf(::A)

                // 为了更像 Koin，我们用 scope(SubClass::class)
                scope(SubClass::class) {

                    scopedOf(::B)
                }
            },
        )
    }
}