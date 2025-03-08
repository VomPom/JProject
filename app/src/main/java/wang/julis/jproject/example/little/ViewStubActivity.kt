package wang.julis.jproject.example.little

import android.view.View
import android.view.ViewStub
import android.widget.Button
import android.widget.ImageView
import com.julis.wang.R
import wang.julis.jwbase.basecompact.BaseActivity

/**
 *
 * Created by @juliswang on 2025/03/03 11:48
 *
 * @Description   ViewStub.layout#setWillNotDraw()#inflate()#
 */
class ViewStubActivity : BaseActivity() {
    private lateinit var viewStub: ViewStub
    private var inflatedView: View? = null
    private var stubImageView: ImageView? = null

    override fun initView() {
        viewStub = findViewById(R.id.view_stub)
        findViewById<Button>(R.id.btn_show).setOnClickListener { showViewStub() }
        findViewById<Button>(R.id.btn_hidden).setOnClickListener { hideViewStub() }
        findViewById<Button>(R.id.btn_change).setOnClickListener { changeViewStub() }
    }

    private fun check() {
        if (stubImageView == null) {
            stubImageView = inflatedView?.findViewById(R.id.stub_img)
        }
    }

    /**
     * 核心在 inflate 方法
     * inflateViewNoAdd() 创建目标 View#layoutId
     * 移除当前 View 添加创建的 View
     */
    private fun showViewStub() {
        try {
            inflatedView = viewStub.inflate()
        } catch (e: Exception) {
            viewStub.visibility = View.VISIBLE
        }
        check()
        stubImageView?.setImageResource(R.mipmap.sign)
    }

    private fun changeViewStub() {
        check()
        stubImageView?.setImageResource(R.drawable.julis)
    }

    private fun hideViewStub() {
        viewStub.visibility = View.INVISIBLE
    }


    override fun initData() {}

    override fun getContentView(): Int = R.layout.activity_view_stub
}