package julis.wang.kotlinlearn.feature

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import wang.julis.jwbase.basecompact.IBaseTest

/**
 * Created by juliswang on 2022/8/1 17:17
 *
 * Description :
 *      什么是协程？
 *              协程就像非常轻量级的线程。线程是由系统调度的，线程切换或线程阻塞的开销都比较大。
 *              而协程依赖于线程，但是协程挂起时不需要阻塞线程，几乎是无代价的，协程是由开发者控制的。所
 *              以协程也像用户态的线程，非常轻量级，一个线程中可以创建任意个协程。
 */
object CoroutinesBaseTest : IBaseTest() {

    /**
     * 1 基础用法
     * 测试协程几个方法获取返回值
     */
    private fun create() {
        // 启动的是一个新的协程并阻塞调用它的线程
        val runBlockingJob = runBlocking<String> {
            Log.d("runBlocking", "返回值为 runBlocking 里面最后一行的数据")
            "runBlocking return value"
        }
        Log.d("runBlockingJob", runBlockingJob)

        val launchJob = GlobalScope.launch {
            Log.d("launch", "返回一个 Job 类型")
            "test"     // 不会返回
        }
        Log.d("launchJob", "$launchJob")


        val asyncJob = GlobalScope.async<String> {
            Log.d("async", "返回一个 Deferred 类型-一个携带有返回值Job")
            "我是返回值"
        }
        Log.d("asyncJob", "$asyncJob 是一个Job，不是真正的返回值")
        Log.d("asyncJob", "asyncJob.await() 执行完之后才能获取到返回值")
        val asyncJobReturn = runBlocking<String> {
            asyncJob.await()
        }
        Log.d("asyncJob", "asyncJob.await() 结果:【${asyncJobReturn}】")
        Log.d("asyncJob", "asyncJob.await() 必须在一个 CoroutineScope 中才能执行")
    }


    /**
     * 2 协程调度器
     *
     * 四种协程调度器
     *  Dispatchers.Main：
     *  这个调度器用于在Android的主线程上执行协程。它适用于所有与UI相关的操作，如更新UI元素、处理用户输入等。
     *  在协程中使用Dispatchers.Main可以确保UI操作在主线程上执行，避免线程错误。
     *
     *  Dispatchers.IO：
     *  这个调度器用于执行阻塞性的I/O操作，如文件读写、网络请求等。它使用一个优化的线程池来执行这些操作，以避免阻塞主线程。
     *  在协程中使用Dispatchers.IO可以确保I/O操作在后台线程上执行，提高应用的响应性能。
     *
     *  Dispatchers.Default：
     *  这个调度器用于执行一般的计算密集型任务。它使用一个共享的线程池来执行协程，适用于那些不需要特定执行环境的任务。
     *  在协程中使用Dispatchers.Default可以确保计算密集型任务在后台线程上执行，避免阻塞主线程。
     *
     *  Dispatchers.Unconfined：这是一个非约束的调度器，它不会限制协程在哪个线程上执行。
     *  使用Dispatchers.Unconfined时，协程将在启动它的线程上执行。这个调度器通常用于一些特殊的场景，如需要在特定线程上执行协程的情况。
     *
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun dispatchers() {
        // A coroutine dispatcher that is confined to the Main thread operating with UI objects.
        GlobalScope.launch(Dispatchers.Main) {}

        // The [CoroutineDispatcher] that is designed for offloading blocking IO tasks to a shared pool of threads.
        GlobalScope.launch(Dispatchers.IO) {}

        // 用于计算密集型任务,使用一个共享的线程池来执行协程
        GlobalScope.launch(Dispatchers.Default) {}

        // 协程将在启动它的线程上执行
        GlobalScope.launch(Dispatchers.Unconfined) {}
    }

    /**
     * 3 协程上下文
     *
     * 协程上下文是一个 CoroutineContext 接口的实现，通常由多个元素组成。这些元素可以通过 + 操作符组合。
     *
     * 常见的协程上下文元素包括：
     *
     * **Job**：管理协程的生命周期，如启动、取消等。
     * **Dispatcher**：决定协程在哪个线程或线程池中执行。
     * **CoroutineName**：为协程命名，便于调试和日志记录。
     * **CoroutineExceptionHandler**：处理协程中的未捕获异常。
     */
    @OptIn(DelicateCoroutinesApi::class)
    private fun withContext() {
        GlobalScope.launch(Dispatchers.Main) {
            val result = withContext(Dispatchers.IO) {
                // 网络请求...
                "请求结果"
            }
            // btn.text = result

            val context1 = Dispatchers.Default
            val context2 = CoroutineName("MyCoroutine")
            val combinedContext = context1 + context2
        }

    }

    /**
     * 4 协程启动模式
     *  CoroutineStart协程启动模式，是启动协程时需要传入的第二个参数。协程启动有4种：
     *  DEFAULT
     *  默认启动模式，我们可以称之为饿汉启动模式，因为协程创建后立即开始调度，虽然是立即调度，单不是立即执行，有可能在执行前被取消。
     *
     *  LAZY
     *  懒汉启动模式，启动后并不会有任何调度行为，直到我们需要它执行的时候才会产生调度。
     *  也就是说只有我们主动的调用Job的start、join或者await等函数时才会开始调度。
     *
     *  ATOMIC
     *  一样也是在协程创建后立即开始调度，但是它和DEFAULT模式有一点不一样，
     *  通过ATOMIC模式启动的协程执行到第一个挂起点之前是不响应cancel 取消操作的，ATOMIC一定要涉及到协程挂起后cancel 取消操作的时候才有意义。
     *
     *  UNDISPATCHED
     *  协程在这种模式下会直接开始在当前线程下执行，直到运行到第一个挂起点。
     *  这听起来有点像 ATOMIC，不同之处在于 UNDISPATCHED 是不经过任何调度器就开始执行的。当然遇到挂起点之后的执行，将取决于挂起点本身的逻辑和协程上下文中的调度器。
     *
     */
    private fun launchMode() {
        // DEFAULT 默认启动模式，我们可以称之为饿汉启动模式，因为协程创建后立即开始调度，虽然是立即调度，单不是立即执行，有可能在执行前被取消。
        val defaultJob = GlobalScope.launch {
            Log.d("defaultJob", "CoroutineStart.DEFAULT")
        }
        defaultJob.cancel()

        // LAZY 懒汉启动模式，启动后并不会有任何调度行为，直到我们需要它执行的时候才会产生调度。
        // 也就是说只有我们主动的调用Job的start、join或者await等函数时才会开始调度。
        val lazyJob = GlobalScope.launch(start = CoroutineStart.LAZY) {
            Log.d("lazyJob", "CoroutineStart.LAZY")
        }
        lazyJob.start()

        // ATOMIC 一样也是在协程创建后立即开始调度，但是它和DEFAULT模式有一点不一样，
        // 通过ATOMIC模式启动的协程执行到第一个挂起点之前是不响应cancel 取消操作的，ATOMIC一定要涉及到协程挂起后cancel 取消操作的时候才有意义。
        val atomicJob = GlobalScope.launch(start = CoroutineStart.ATOMIC) {
            Log.d("atomicJob", "CoroutineStart.ATOMIC挂起前")
            delay(100)
            Log.d("atomicJob", "CoroutineStart.ATOMIC挂起后")
        }
        atomicJob.cancel()

        // UNDISPATCHED 协程在这种模式下会直接开始在当前线程下执行，直到运行到第一个挂起点。
        // 这听起来有点像 ATOMIC，不同之处在于 UNDISPATCHED 是不经过任何调度器就开始执行的。
        // 当然遇到挂起点之后的执行，将取决于挂起点本身的逻辑和协程上下文中的调度器。
        val undispatchedJob = GlobalScope.launch(start = CoroutineStart.UNDISPATCHED) {
            Log.d("undispatchedJob", "CoroutineStart.UNDISPATCHED挂起前")
            delay(100)
            Log.d("undispatchedJob", "CoroutineStart.UNDISPATCHED挂起后")
        }
        undispatchedJob.cancel()
    }

    /**
     * 5 协程作用域
     * 顶级作用域 -->
     * 没有父协程的协程所在的作用域称之为顶级作用域。
     *
     * 协同作用域 -->
     * 在协程中启动一个协程，新协程为所在协程的子协程。子协程所在的作用域默认为协同作用域。
     * 此时子协程抛出未捕获的异常时，会将异常传递给父协程处理，如果父协程被取消，则所有子协程同时也会被取消。
     *
     * 主从作用域  -->
     * 官方称之为监督作用域。与协同作用域一致，区别在于该作用域下的协程取消操作的单向传播性，子协程的异常不会导致其它子协程取消。
     * 但是如果父协程被取消，则所有子协程同时也会被取消。
     */
    // 父协程需要等待所有的子协程执行完毕之后才会进入Completed状态
    //                                       wait children
    //  +-----+ start  +--------+ complete   +-------------+  finish  +-----------+
    //  | New | -----> | Active | ---------> | Completing  | -------> | Completed |
    //  +-----+        +--------+            +-------------+          +-----------+
    //                   |  cancel / fail       |
    //                   |     +----------------+
    //                   |     |
    //                   V     V
    //               +------------+                           finish  +-----------+
    //               | Cancelling | --------------------------------> | Cancelled |
    //               +------------+                                   +-----------+
    @OptIn(DelicateCoroutinesApi::class)
    private fun scope() {
        GlobalScope.launch(Dispatchers.Main) {
            Log.d("父协程上下文", "$coroutineContext")
            launch(CoroutineName("第一个子协程")) {
                Log.d("第一个子协程上下文", "$coroutineContext")
            }
            launch(Dispatchers.Unconfined) {
                Log.d("第二个子协程协程上下文", "$coroutineContext")
            }
        }
    }

    /**
     * 6 挂起函数
     * 挂起函数只能在协程体内，或着在其他挂起函数内调用
     *
     * byte code:
     * private final testSuspend(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;
     *
     */
    private suspend fun testSuspend() {}

    override fun run(context: Context) {
        create()
        dispatchers()
        withContext()
        launchMode()
        scope()
//        testSuspend()
    }


}










