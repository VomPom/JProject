package wang.julis.jproject.example.source.koin.parameter3

import wang.julis.jproject.example.ComponentInt
import wang.julis.jproject.example.ComponentIntFloat

/**
 *
 * Created by @juliswang on 2025/08/19 21:07
 *
 * @Description
 */
fun main() {
    withParameter()
}

fun withParameter() {
    val scopeQualifier = "scopeQualifier"
    val koin = startKoin {
        modules(
            module {
                single { (data: Int) -> ComponentInt(data) }
                factory { (data1: Int, data2: Float) -> ComponentIntFloat(data1, data2) }

                scope(scopeQualifier) {
                    scoped { (data: Int) -> ComponentInt(data) }
                }
            }
        )
    }.koin
    val componentInt1 = koin.get<ComponentInt> { parametersOf(123) }

    println("componentInt1 hashCode:${componentInt1.hashCode()}")
    println("componentInt1 data value:${componentInt1.data}")

    val componentInt2 = koin.get<ComponentInt> { parametersOf(999) }
    println("componentInt2 hashCode:${componentInt2.hashCode()}")
    println("componentInt2 data value:${componentInt1.data}")
    // componentInt1.hashcode == componentInt2.hashcode


    val componentIntFloat = koin.get<ComponentIntFloat> { parametersOf(123, 1.0f) }
    println("componentIntFloat hashCode:${componentIntFloat.hashCode()}")
    println("componentIntFloat int value float:${componentIntFloat.data1},${componentIntFloat.data2}")

    val componentIntFloat2 = koin.get<ComponentIntFloat> { parametersOf(999, 999.0f) }
    println("componentIntFloat2 hashCode:${componentIntFloat2.hashCode()}")
    println("componentIntFloat2 int value float:${componentIntFloat2.data1},${componentIntFloat2.data2}")


    val scope = koin.createScope("scope", scopeQualifier)
    val scopeComponentInt = scope.get<ComponentInt> { parametersOf(456) }
    println("componentIntFloat hashCode:${scopeComponentInt.hashCode()}")
    println("scopeComponentInt data value:${scopeComponentInt.data}")
}
