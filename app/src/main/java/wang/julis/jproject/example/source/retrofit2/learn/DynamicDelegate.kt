package wang.julis.jproject.example.source.retrofit2.learn

import android.content.Context
import wang.julis.jwbase.basecompact.IBaseTest
import wang.julis.jwbase.utils.Logger
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Proxy

/**
 *
 * Created by @juliswang on 2025/03/13 20:45
 *
 * @Description 动态代理的实现
 *
 *  https://juejin.cn/post/6844903744954433544
 */
object DynamicDelegate : IBaseTest() {
    data class TestData(val msg: String)
    interface TestApi {
        fun testFun(msg: String): TestData
    }

    interface TestApi2 {
        fun testFun(msg: String)
    }

    class TestApiImpl : TestApi {
        override fun testFun(msg: String): TestData {
            Logger.d("TestApiImpl testFun invoke.")
            return TestData(msg)
        }

    }

    // 静态代理的实现
    // 优点：
    //  实现简单，易于理解和调试。
    //  可以在不修改被代理类的情况下，增加额外的功能。
    // 缺点：
    //  如果需要为多个类创建代理，会导致代理类数量急剧增加，维护成本高。
    //  编译时就需要确定代理关系，缺乏灵活性。
    class TestApiStaticProxy(private val target: TestApi) :
        TestApi {
        override fun testFun(msg: String): TestData {
            beforeInvoke()
            val r = target.testFun(msg)
            afterInvoke()
            return r
        }

        private fun beforeInvoke() {
            // no-op
        }

        private fun afterInvoke() {
            // no-op
        }
    }

    // 动态代理的实现
    // 优点：
    //  灵活性高，可以为任意接口生成代理类，无需为每个接口编写代理类。
    //  减少了代码量，提高了代码的可维护性和可扩展性。
    // 缺点：
    //  相对于静态代理，理解和实现起来稍微复杂一些。
    //  由于是在运行时生成代理类，可能会带来一定的性能开销。
    class DynamicProxy(private val impl: Any) {

        private val invocationHandler = InvocationHandler { proxy, method, args ->
            beforeInvoke()
            val returnObj = method.invoke(impl, *(args ?: emptyArray()))
            afterInvoke()
            returnObj
        }

        fun proxy(): Any {
            /**
             * loader：    用于指定用哪个类加载器，去加载生成的代理类；
             * interfaces：指定这个代理类能够代理目标对象的哪些方法和接口；
             * h：         用来指定生成的代理对象在方法被调用时如何进行处理；
             */
            return Proxy.newProxyInstance(
                impl.javaClass.classLoader,
                impl.javaClass.interfaces,
                invocationHandler
            )
        }

        private fun beforeInvoke() {
            Logger.d("proxy before invoke.")
        }

        private fun afterInvoke() {
            Logger.d("proxy after invoke.")
        }
    }

    override fun run(context: Context) {
        val testApiImpl = TestApiImpl()

        val testApiImpl2 = object : TestApi2 {
            override fun testFun(msg: String) {
                Logger.d("TestApiImpl2 testFun invoke.")
            }
        }

        val testApiStaticProxy = TestApiStaticProxy(testApiImpl)

        // 动态代理实现
        val proxy = DynamicProxy(testApiImpl).proxy()
        val returnUnit = (proxy as TestApi).testFun("this is s message.")
        Logger.d("got return obj:${returnUnit.msg}")

        // 如果这时候要为新的一个类进行代理的话继续使用动态代理的逻辑即可
        // 无需再创建 [TestApiStaticProxy] 这样的新类
        val proxy2 = DynamicProxy(testApiImpl2)
            .proxy() as TestApi2
        proxy2.testFun("proxy2 test func invoke.")
    }
}






