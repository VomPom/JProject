package wang.julis.jproject.example.little

import android.widget.Button
import com.julis.router.Router
import com.julis.wang.R
import wang.julis.jproject.MainActivity
import wang.julis.jwbase.basecompact.BaseActivity

/**
 *
 * Created by @juliswang on 2024/04/30 11:48
 *
 * @Description
 */

class RouterActivity : BaseActivity() {
    companion object {
        const val HOST = "Router"
    }

    override fun initView() {
        findViewById<Button>(R.id.btn_start).setOnClickListener {
            Router.open(this@RouterActivity, MainActivity.HOST)
        }
    }

    override fun initData() {
    }

    override fun getContentView(): Int {
        return R.layout.activity_kotlin_test
    }
}