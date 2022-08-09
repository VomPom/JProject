package julis.wang.kotlinlearn.feature

import android.widget.Button
import julis.wang.kotlinlearn.R
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
        }
    }

    private fun testCoroutines() {

    }

    override fun initData() {

    }

    override fun getContentView(): Int {
        return R.layout.activity_kotlin_test
    }


}