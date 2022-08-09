package wang.julis.learncpp.page

import android.widget.TextView
import wang.julis.jwbase.basecompact.BaseActivity
import wang.julis.learncpp.R
import wang.julis.learncpp.ops.CacheFieldAndMethodOps

/**
 * Created by juliswang on 2022/7/29 17:20
 *
 * Description :
 *
 *
 */

class MethodActivity : BaseActivity() {
    private val cacheFieldAndMethodOps = CacheFieldAndMethodOps()
    override fun initView() {
        findViewById<TextView>(R.id.btn_command).setOnClickListener {
            cacheFieldAndMethodOps.invoke()
        }

    }

    override fun initData() {
    }


    override fun getContentView(): Int {
        return R.layout.activity_method
    }


}