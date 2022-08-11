package wang.julis.learncpp

import android.app.NativeActivity
import wang.julis.jwbase.basecompact.baseList.BaseListActivity
import wang.julis.learncpp.bitmap.BitmapOperationActivity
import wang.julis.learncpp.page.*

/**
 * Created by juliswang on 2022/7/29 12:41
 *
 * Description :
 *
 *
 */

class CppMainActivity : BaseListActivity() {
    override fun initData() {
        addActivity("基础操作", LittleJniActivity::class.java)
        addActivity("JniCallback", JniCallbackActivity::class.java)
        addActivity("NativeActivity", NativeActivity::class.java)
        addActivity("NativeWebP", WebPNativeActivity::class.java)

        addActivity("Bitmap操作", BitmapOperationActivity::class.java)
        addActivity("方法属性", MethodActivity::class.java)
        addActivity("多线程", ThreadActivity::class.java)
        addActivity("设计模式", DesignActivity::class.java)
    }

}