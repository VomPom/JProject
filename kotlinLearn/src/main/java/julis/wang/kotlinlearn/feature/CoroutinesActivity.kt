package julis.wang.kotlinlearn.feature

import android.util.Log
import android.widget.Button
import julis.wang.kotlinlearn.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import wang.julis.jwbase.basecompact.BaseActivity

/**
 * Created by juliswang on 2022/8/1 17:17
 *
 * Description :
 *          suspending functions:
 *          functions that can stop the execution when they are called
 *          and make it continue once it has finished running their background task.
 *
 *
 */
class CoroutinesActivity : BaseActivity() {
    private val TAG = "--julis"
    override fun initView() {
        findViewById<Button>(R.id.btn_start).setOnClickListener {
            testCoroutines()
        }
    }

    private fun testCoroutines() {
        runBlocking { // this: CoroutineScope
            launch { // launch a new coroutine and continue
                delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
                Log.e(TAG, "World!") // print after delay
            }
            Log.e(TAG, "Hello") // main coroutine continues while a previous one is delayed
        }
    }

    override fun initData() {

    }

    override fun getContentView(): Int {
        return R.layout.activity_kotlin_test
    }


}