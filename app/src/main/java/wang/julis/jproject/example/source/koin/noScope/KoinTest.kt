package wang.julis.jproject.example.source.koin.noScope

import android.content.Context
import wang.julis.jproject.example.ComponentA
import wang.julis.jproject.example.ComponentB
import wang.julis.jwbase.basecompact.IBaseTest

/**
 *
 * Created by @juliswang on 2025/08/19 21:07
 *
 * @Description
 */
fun main() {
    withoutScope1()
}

object KoinTest : IBaseTest() {
    override fun run(context: Context) {
        withoutScope1()
        withoutScope2()
    }
}

fun withoutScope1() {
    startKoin {
        modules(
            module {
                single { ComponentA() }
                single("qualifier") { ComponentA() }
                factory { ComponentB(get()) }
            }
        )
    }

    val koin = GlobalContext.get()
    val componentA1 = koin.get<ComponentA>()
    val componentA2 = koin.get<ComponentA>()
    val componentA3 = koin.get<ComponentA>("qualifier")
    if (componentA1.hashCode() != componentA2.hashCode()) {
        throw RuntimeException("componentA1.hashCode() != componentA2.hashCode().")
    }
    if (componentA1.hashCode() == componentA3.hashCode()) {
        throw RuntimeException("componentA1.hashCode() == componentA3.hashCode().")
    }

    println("componentA1 hashCode:${componentA1.hashCode()}")
    println("componentA2 hashCode:${componentA2.hashCode()}")
    println("componentA3 hashCode:${componentA3.hashCode()}")

//    val componentB = koin.get<ComponentB>()   !! can't be work!

}

fun withoutScope2() {
    // 另一种方式使用
    val koin = koinApplication {
        modules(
            module {
                single { ComponentA() }
            }
        )
    }.koin

    val componentA1 = koin.get<ComponentA>()
    val componentA2 = koin.get<ComponentA>()
    if (componentA1.hashCode() != componentA2.hashCode()) {
        throw RuntimeException("generate single instance error.")
    }
}
