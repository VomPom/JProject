package wang.julis.jproject.example.little

import android.content.Context
import android.content.Intent
import com.julis.wang.R
import wang.julis.jwbase.basecompact.BaseActivity

/**
 *
 * Created by @juliswang on 2025/03/13 10:09
 *
 * @Description
 */
class UtilsShowActivity : BaseActivity() {
    companion object {
        fun start(context: Context, data: String?) {
            val intent = Intent(context, UtilsShowActivity::class.java)
            data?.let {
                intent.putExtra("KEY_DATA", it)
            }
            context.startActivity(intent)
        }
    }

    override fun initView() {}

    override fun initData() {}

    override fun getContentViewId(): Int {
        return R.layout.activity_kotlin_test
    }

}