package wang.julis.learncpp.common

import android.util.Log

/**
 * Created by juliswang on 2022/7/29 18:42
 *
 * Description :
 *
 *
 */

object LogUtil {
    public fun d(msg: String) {
        Log.d("--julis", "msg:$msg")
    }
    public fun e(msg: String) {
        Log.e("--julis", "msg:$msg")
    }
}