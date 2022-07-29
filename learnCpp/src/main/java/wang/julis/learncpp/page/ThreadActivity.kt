package wang.julis.learncpp.page

import android.widget.TextView
import wang.julis.jwbase.basecompact.BaseActivity
import wang.julis.learncpp.R
import wang.julis.learncpp.ops.ThreadOps

/**
 * Created by juliswang on 2022/7/29 17:20
 *
 * Description :
 *
 *
 */

class ThreadActivity : BaseActivity() {
    private val threadOps = ThreadOps()
    override fun initView() {
        findViewById<TextView>(R.id.btn_start).setOnClickListener {
            threadOps.invoke()
        }
    }

    override fun initData() {

    }


    override fun getContentView(): Int {
        return R.layout.activity_method
    }


}