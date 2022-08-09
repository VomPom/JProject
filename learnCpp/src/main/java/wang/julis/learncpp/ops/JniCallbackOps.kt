package wang.julis.learncpp.ops

import android.app.Activity
import wang.julis.learncpp.BaseOperation

/**
 * Created by juliswang on 2022/8/2 20:51
 *
 * Description :
 *
 *
 */

class JniCallbackOps : BaseOperation() {
    override fun invoke() {

        TODO("Not yet implemented")
    }

    external fun stringFromJNI(): String?
    external fun startTicks(activity: Activity)
    external fun stopTicks()
}