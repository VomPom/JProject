package wang.julis.learncpp

import android.app.NativeActivity
import wang.julis.jwbase.basecompact.baseList.BaseListActivity
import wang.julis.learncpp.bitmap.BitmapOperationActivity
import wang.julis.learncpp.mediacodec.NativeCodecActivity
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
        addItem("基础操作", LittleJniActivity::class.java)

        addItem("JniCallback", JniCallbackActivity::class.java)
        addItem("NativeActivity", NativeActivity::class.java)
        addItem("NativeWebP", WebPNativeActivity::class.java)
        addItem("NativeMediaCodec", NativeCodecActivity::class.java)

        addItem("Bitmap操作", BitmapOperationActivity::class.java)
        addItem("方法属性", MethodActivity::class.java)
        addItem("多线程", ThreadActivity::class.java)
        addItem("设计模式", DesignActivity::class.java)
    }

}