package wang.julis.learncpp.page

import android.widget.Button
import android.widget.TextView
import androidx.annotation.Keep
import wang.julis.jwbase.basecompact.BaseActivity
import wang.julis.learncpp.R
import wang.julis.learncpp.ops.JniCallbackOps


/**
 * Created by juliswang on 2022/8/2 20:53
 *
 * Description :
 *
 *
 */

class JniCallbackActivity : BaseActivity() {
    private var hour = 0
    private var minute = 0
    private var second = 0
    private var tickView: TextView? = null
    private var jnicallback = JniCallbackOps()

    override fun initView() {
        tickView = findViewById(R.id.tickView)
        findViewById<Button>(R.id.btn_stop).setOnClickListener {
            jnicallback.stopTicks()
        }
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        second = 0
        minute = second
        hour = minute
        jnicallback.startTicks(this)
    }

    override fun onPause() {
        super.onPause()
        jnicallback.stopTicks()
    }

    override fun getContentView(): Int {
        return R.layout.acitivity_jni_callback
    }

    /*
     * A function calling from JNI to update current timer
     */
    @Keep
    private fun updateTimer() {
        ++second
        if (second >= 60) {
            ++minute
            second -= 60
            if (minute >= 60) {
                ++hour
                minute -= 60
            }
        }
        runOnUiThread {
            val ticks = "" + this@JniCallbackActivity.hour.toString() + ":" +
                this@JniCallbackActivity.minute.toString() + ":" +
                this@JniCallbackActivity.second
            this@JniCallbackActivity.tickView?.text = ticks
        }
    }
}