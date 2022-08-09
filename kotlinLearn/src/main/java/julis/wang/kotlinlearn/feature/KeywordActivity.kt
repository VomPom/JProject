package julis.wang.kotlinlearn.feature

import android.app.Application
import android.util.Log
import android.widget.Button
import julis.wang.kotlinlearn.R
import julis.wang.kotlinlearn.jetpack.Address
import julis.wang.kotlinlearn.jetpack.User
import wang.julis.jwbase.basecompact.BaseActivity

/**
 * Created by juliswang on 2022/8/1 14:10
 *
 * Description :
 *
 *
 */

class KeywordActivity : BaseActivity() {
    companion object {
        private var instance: Application? = null
        fun instance() = instance!!
    }

    private val TAG = "--julis"
    override fun initView() {
        findViewById<Button>(R.id.btn_start).setOnClickListener {
            testKeyWord(User(0, "juliswang", Address("address_id", "shanghai pudong...")))
        }
    }

    override fun initData() {

    }

    override fun getContentView(): Int {
        return R.layout.activity_kotlin_test
    }

    private fun testKeyWord(user: User?) {
        testLet(user)
        testRun(user)
        testWith(user)
        testApply(user)
        testAlso(user)
        testTakeIf(user)
        takeUnless(user)
        testRunCatching(user)
        testInline(user)

        testFun()
    }

    /**
     * Calls the specified function [block] with `this` value as its argument and returns its result.
     * 将当前对象作为方法的入参，返回值为let块的最后一行或指定return表达式。
     *
     * 1\let块中的最后一条语句如果是非赋值语句，则默认情况下它是返回语句，反之，则返回的是一个 Unit类型
     * 2\let可用于空安全检查
     * 3\let可对调用链的结果进行操作
     * 4\let可以将“It”重命名为一个可读的lambda参数
     *
     * 【实用场景】-> 适用于对象统一处理不为空的情况
     *
     * @param user
     */
    private fun testLet(user: User?) {
        user?.let {
            it.name = "new_name"
            it.address  //　作为返回值返回,等价于 下面address的实现
        }.let {
            Log.e(TAG, "it.address detail by it:${it?.detail}")
            it?.detail
        }.let { detail ->
            Log.e(TAG, "it.address detail by detail word:${detail}")
        }.let {
            //这时候的值就是 Log.e的返回值
        }

        val address: Address? = user?.let {
            it.name = "new_name"
            it.address  //　作为返回值返回
        }
    }

    /**
     * Calls the specified function [block] with the given [receiver] as its receiver and returns its result.
     * with()函数是一个内联函数，它把传入的对象作为接受者，在该函数内可以使用this指代该对象来访问其公有的属性和方法。
     * 该函数的返回值为函数块最后一行或指定的return表示式。
     *
     * 【实用场景】-> 适用于调用同一个类多个方法
     * @param user
     */
    private fun testWith(user: User?) {
        with(user) {
            this?.address
        }?.let {
            Log.e(TAG, "it.address detail by detail word:${it.detail}")
        }
        val newUser = testWithAnother(user)
        newUser?.name
    }

    private fun testWithAnother(user: User?) = with(user) {
        user?.name = "new_name"
        this
    }

    /**
     * Calls the specified function [block] with `this` value as its receiver and returns its result.
     *
     * with 跟 let的结合体
     *
     * 【实用场景】-> 适用with()、let()函数的任何场景
     * @param user
     */
    private fun testRun(user: User?) {
        user?.run {
            address
        }.run {
            this?.detail
        }
    }

    /**
     * Calls the specified function [block] with `this` value as its receiver and returns `this` value.
     * apply()函数和run()函数相似，不同的是，run()函数是以闭包形式返回最后一行代码的值，而apply()函数返回的是传入的对象本身。
     *
     * 【实用场景】-> 适用于run()函数的任何场景，通常可用来在初始化一个对象实例时，操作对象属性并最终返回该对象。
     *              也可用于多个扩展函数链式调用
     * @param user
     */
    private fun testApply(user: User?) {
        val newUser = user.apply {
            this?.name = "new_user_name"
        }.apply {
            //返回对象本身
        }.apply {
            //返回对象本身
        }
        Log.e(TAG, "user:${newUser?.name}")
    }

    /**
     * Calls the specified function [block] with `this` value as its argument and returns `this` value.
     *
     * also()函数和apply()函数相似，不同的是，also()函数在函数块中使用it指代该对象，而apply()函数在函数块中使用this指代该对象。
     *
     * 【实用场景】-> 适用于let()函数的任何场景，一般可用于多个扩展函数链式调用
     * @param user
     */
    private fun testAlso(user: User?) {
        val newUser = user.apply {
            this?.name = "new_user_name"
        }.also {
            //返回对象本身
        }
    }

    /**
     * Returns `this` value if it satisfies the given [predicate] or `null`, if it doesn't.
     * 如果满足条件则返回对应的对象，否则返回null
     *
     * @param user
     */
    private fun testTakeIf(user: User?) {
        user.takeIf {
            it?.name == "juliswang"
        }?.let {
            it.name = "new_name" //这里的it一定不为空
        }
    }

    /**
     * Returns `this` value if it _does not_ satisfy the given [predicate] or `null`, if it does.
     * 与 takeIf 作用完全相反
     * @param user
     */
    private fun takeUnless(user: User?) {
        user.takeUnless { it?.name == "juliswang" }.apply { }
    }

    private fun testRunCatching(user: User?) {
        val isSuccess = user.runCatching { 1 / 0 }.isSuccess
        user.runCatching { 1 / 0 }.exceptionOrNull()
        Log.e(TAG, "run isSuccess:$isSuccess")
    }

    private fun testFun() {
        val value = testFun { str, intValue ->
            Log.e(TAG, "str:$str")
            100 + intValue
        }
        Log.e(TAG, "value:$value")

        //其他展示方式
        testFun({ _, _ -> 0 })
        testFun() { _, _ -> 0 }
        testFun { _, _ -> 0 }
    }

    private fun testFun(block: (String, Int) -> Int): Int {
        return block("str", 0)
    }

    private fun testInline(user: User?) {
        val returnStr = newMethod(user) {
            1
        }
        Log.e(TAG, "testInline returnStr:$returnStr")
    }

    private inline fun <T> newMethod(type: T, body: T.() -> Int): String {
        val value: Int = type.body()
        listOf<Int>()
        val arrayList = ArrayList<Int>()

        return "new method value:$value"
    }

    class TestValue {

        private val lazyValue: String by lazy {
            "getString()"
        }
    }


}

















