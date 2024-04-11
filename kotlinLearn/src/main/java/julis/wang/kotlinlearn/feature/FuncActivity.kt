package julis.wang.kotlinlearn.feature

import android.util.Log
import android.widget.Button
import julis.wang.kotlinlearn.R
import julis.wang.kotlinlearn.feature.FuncActivity.Companion.TAG
import wang.julis.jwbase.basecompact.BaseActivity

/**
 * Created by @juliswang on 2024/04/08 19:50
 *
 * @Description 对 Kotlin 里面的高阶函数做一些试验
 */
class FuncActivity : BaseActivity() {
    companion object {
        const val TAG = "-Func"
    }

    override fun initView() {
        findViewById<Button>(R.id.btn_start).setOnClickListener {
            test()
        }
    }

    override fun initData() {

    }

    override fun getContentView(): Int {
        return R.layout.activity_kotlin_test
    }

    private fun test() {

        val action = SubAction()

        simpleFunc()

        simpleFunc2 {
            Log.e(TAG, "call simpleFunc2.")
        }

        simpleFunc3 {
            it()
        }

        complexFunc1(action) {
            Log.e(TAG, "nothing to do.")
        }

        val complexFunc2Result = complexFunc2(action) {
            Log.e(TAG, "will call  function")
        }
        complexFunc2Result()

        val complexFunc3Result = complexFunc3(action) { baseAction ->
            Log.e(TAG, "complexFunc3 got base action$baseAction")
        }

        complexFunc3Result()

        val complexFunc4Result = complexFunc4(action) { baseAction ->
            {
                Log.e(TAG, "complexFunc4 got base action$baseAction")
            }
        }

        complexFunc4Result()

        val complexFunc5Result = complexFunc5(action) {
            { baseAction, function ->
                function()
                Log.e(TAG, "complexFunc5 got base action$baseAction")
            }
        }

        complexFunc5Result(action) {
            Log.e(TAG, "complexFunc5Result func continue.")
        }

        val complexFunc6Result = complexFunc6(simpleFunc) {
            simpleFunc
        }
        complexFunc6Result()

        val complexFunc7Result = complexFunc7({
            1
        }, {
            2
        })

        Log.e(TAG, "complexFunc7Result :$complexFunc7Result")

        complexFunc8(
            { baseAction ->
                Log.e(TAG, "complexFunc7 baseAction1:" + (baseAction as SubAction).outputSth(""))
            }
        ) { baseAction2 ->
            Log.e(TAG, "complexFunc7 baseAction2:" + (baseAction2 as SubAction).outputSth(""))
        }
    }


    val simpleFunc: UnitFunc = {
        Log.e(TAG, "this is s simple action")
    }

    val simpleFunc2: UnitFunc2 = { func ->
        func()
    }

    val simpleFunc3: UnitFunc3 = { func ->
        func {
            Log.e(TAG, "inner func")
        }
    }

    val complexFunc1: ComplexFunc = { baseAction, function ->
        if (baseAction is SubAction) {
            baseAction.outputSth("complexFunc1")
        }
        function()
    }

    val complexFunc2: ComplexFunc2 = { baseAction, function ->
        if (baseAction is SubAction) {
            baseAction.outputSth("complexFunc2")
        }
        {
            function()
        }
    }

    val complexFunc3: ComplexFunc3 = { baseAction, function ->
        if (baseAction is SubAction) {
            baseAction.outputSth("complexFunc3")
        }
        {
            function(baseAction)
        }
    }

    val complexFunc4: ComplexFunc4 = { baseAction, function ->
        if (baseAction is SubAction) {
            baseAction.outputSth("complexFunc4")
        }
        function(baseAction)
    }

    val complexFunc5: ComplexFunc5 = { baseAction, function1 ->
        if (baseAction is SubAction) {
            baseAction.outputSth("complexFunc5")
        }
        function1(baseAction)
    }

    val complexFunc6: ComplexFunc6 = { function1, function2 ->
        function1()
        function2(SubAction())
    }

    val complexFunc7: ComplexFunc7 = { function1, function2 ->
        val result1 = function1()
        val result2 = function2()
        result1 + result2
    }

    val complexFunc8: ComplexFunc8 = { function1, function2 ->

        val action = SubAction()
        function1(action)
        function2(action)
    }

}


typealias UnitFunc = () -> Unit
typealias UnitFunc2 = (() -> Unit) -> Unit
typealias UnitFunc3 = ((UnitFunc) -> Unit) -> Unit

typealias ComplexFunc = (BaseAction, () -> Unit) -> Unit
typealias ComplexFunc2 = (BaseAction, () -> Unit) -> UnitFunc
typealias ComplexFunc3 = (BaseAction, (BaseAction) -> Unit) -> UnitFunc
typealias ComplexFunc4 = (BaseAction, (BaseAction) -> UnitFunc) -> UnitFunc
typealias ComplexFunc5 = (BaseAction, (BaseAction) -> ComplexFunc) -> ComplexFunc
typealias ComplexFunc6 = (UnitFunc, (BaseAction) -> UnitFunc) -> UnitFunc
typealias ComplexFunc7 = (() -> Int, () -> Int) -> Int
typealias ComplexFunc8 = ((BaseAction) -> Unit, (BaseAction) -> Unit) -> Unit

interface BaseAction

class SubAction : BaseAction {
    fun outputSth(str: String) {
        Log.e(TAG, "SubAction output:$str")
    }
}
