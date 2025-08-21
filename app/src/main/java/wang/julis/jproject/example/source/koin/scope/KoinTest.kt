package wang.julis.jproject.example.source.koin.scope

import wang.julis.jproject.example.ComponentB

/**
 *
 * Created by @juliswang on 2025/08/19 21:07
 *
 * @Description
 */
fun main() {
    withScope()
}

fun withScope() {
    val scopeQualifier = "scopeQualifier"
    startKoin { }
    val koin = koinApplication {
        modules(
            module {
                scope(scopeQualifier) {
                    scoped {
                        ComponentB(get())
                    }
                }
            }
        )
    }.koin

//    val componentA = koin.get<ComponentA>()

    val scope = koin.createScope("scope", scopeQualifier)
    val componentB = scope.get<ComponentB>()

}