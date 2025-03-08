package julis.wang.kotlinlearn.feature

import android.content.Context
import android.util.Log
import julis.wang.kotlinlearn.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import wang.julis.jwbase.basecompact.IBaseTest
import java.util.UUID

/**
 *
 * Created by @juliswang on 2025/03/05 10:37
 *
 * @Description 这个类主要展示了 flow 的基本用法，和一个结合 Flow 设计的 class 执行类
 */
object CoroutinesFlowTest : IBaseTest() {
    private const val TAG = "CoroutinesFlowTest"

    /**
     * Flow 的主要作用包括：
     *
     * 按顺序发射多个值，并在协程中异步处理这些值。
     * 支持链式操作符（如 map、filter、flatMap 等），可以轻松地对数据流进行转换和组合。
     * 冷流（Cold Stream）：Flow 是冷流，只有在收集（collect）时才会开始执行。
     * 背压（Backpressure）支持：Flow 天然支持背压，能够处理生产者和消费者之间的速度不匹配问题。
     * 与协程无缝集成：Flow 完全基于协程，可以轻松地与协程的其他功能（如取消、异常处理等）结合使用。
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun testFlow() {
        val job = GlobalScope.launch {
            flow {
                for (i in 1..10) {
                    delay(100)
                    emit(i)
                }
            }
                // 中间操作符
                .map { it * 2 }
                .filter { it > 3 }
                .take(5)   // 只取前 N 个值

                // 捕获 Flow 中的异常
                .catch { e ->
                    Log.d(TAG, "exception :$e")
                }
                // 指定 Flow 的执行线程
                .flowOn(Dispatchers.IO)
                // 使用 collect 方法收集 Flow 的值。
                .collect(collector = { value -> Log.d(TAG, "testFlow value :${value}") })
        }

        // Flow 的收集可以被协程的取消操作中断
        // job.cancel()

        // 组合操作符 - zip 基本原则
        // 会等待两个 Flow 都发射一个元素后，将它们组合成一个新元素。
        // 当其中一个 Flow 发射完所有元素后，zip 就会停止，即使另一个 Flow 还有剩余元素。
        GlobalScope.launch {
            val flow1 = flowOf(1, 2, 3, 4)
            val flow2 = flowOf("A", "B", "C")
            flow1.zip(flow2) { a, b -> "$a -> $b" }.collect { value -> Log.d(TAG, "zip value :${value}") }
        }

        // 组合操作符 - combine 基本原则
        // 每当任意一个 Flow 发射新元素时，都会与其他 Flow 的最新值进行组合。
        // 与 zip 不同，combine 不要求 Flow 的元素数量一致，也不会等待所有 Flow 都发射元素后再组合。
        GlobalScope.launch {
            val flow1 = flowOf(1, 2, 3, 4)
            val flow2 = flowOf("A", "B", "C")
            flow1.combine(flow2) { a, b -> "$a -> $b" }
                .collect { value ->
                    Log.d(TAG, "combine value :${value}")
                }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    // 该注解定义 flow 使用的周期“不安全”的，应该使用 viewModelScope and lifecycleScope
    // Marks declarations in the coroutines that are delicate — they have limited use-case and shall be used with care in general code.
    // Any use of a delicate declaration has to be carefully reviewed to make sure it is properly used and does not create problems like memory and resource leaks.
    // Carefully read documentation of any declaration marked as DelicateCoroutinesApi.

    // This is because kotlin coroutines follow a principle of structured concurrency which means that new coroutines
    // can be only launched in a specific CoroutineScope which delimits the lifetime of the coroutine.
    // for example if you start a coroutine with viewModelScope, then this coroutine will be cancelled as soon as ViewModel is destroyed.
    private fun testFlowFunc() {
        GlobalScope.launch {
            RequestUseCase().execute(
                EntityModel(UUID.randomUUID().toString(), "this is data:")
            ).collect {
                it.forEach { result ->
                    Log.d(TAG, result)
                }
            }
        }
    }

    override fun run(context: Context) {
        testFlow()
        testFlowFunc()
    }


    class RequestUseCase : BaseUseCase<String, EntityModel<String>>() {
        override suspend fun execute(params: EntityModel<String>): Flow<List<String>> = flow {
            emit(
                listOf(
                    "${params.data} a-${params.id}",
                    "${params.data} b-${params.id}",
                    "${params.data} c-${params.id}"
                )
            )
        }.flowOn(Dispatchers.IO)
    }

    abstract class BaseUseCase<T, R>() {
        abstract suspend fun execute(params: R): Flow<List<T>>
    }

    data class EntityModel<T>(val id: String, val data: T?)
}