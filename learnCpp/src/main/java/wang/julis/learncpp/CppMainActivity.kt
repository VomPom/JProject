package wang.julis.learncpp

import wang.julis.jwbase.basecompact.baseList.BaseListActivity
import wang.julis.learncpp.bitmap.BitmapOperationActivity
import wang.julis.learncpp.page.MethodActivity
import wang.julis.learncpp.page.ThreadActivity

/**
 * Created by juliswang on 2022/7/29 12:41
 *
 * Description :
 *
 *
 */

class CppMainActivity : BaseListActivity() {
    override fun initData() {
        addActivity("Bitmap操作", BitmapOperationActivity::class.java)
        addActivity("方法属性", MethodActivity::class.java)
        addActivity("多线程", ThreadActivity::class.java)
    }
}