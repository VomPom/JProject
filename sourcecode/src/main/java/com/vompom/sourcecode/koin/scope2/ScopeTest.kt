package com.vompom.sourcecode.koin.scope2

import com.vompom.sourcecode.koin.ComponentA
import com.vompom.sourcecode.koin.ComponentB
import com.vompom.sourcecode.koin.ComponentC


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
                single {
                    ComponentA()
                }
                scope(scopeQualifier) {
                    scoped {
                        ComponentA()
                    }
                    scoped {
                        ComponentB(get())
                    }
                    factory {
                        ComponentC(get())
                    }

                }
            }
        )
    }.koin

    val scope = koin.createScope("scope", scopeQualifier)
    val componentB = scope.get<ComponentB>()
    val componentC = scope.get<ComponentC>()
    val componentAInScope = scope.get<ComponentA>()

    println("componentA in scope hashCode:${componentAInScope.hashCode()}")
    println("componentB.A hashCode:${componentB.a.hashCode()}")
    println("componentC hashCode:${componentC.hashCode()}")
    println("componentB hashCode:${componentB.hashCode()}")
    println("componentC.B hashCode:${componentC.b.hashCode()}")


    val scope2 = koin.createScope("scope2", scopeQualifier)
    val componentAInScope2 = scope2.get<ComponentA>()

    println("componentA in scope2 hashCode:${componentAInScope2.hashCode()}")
}