
package wang.julis.learncpp.common;

import android.os.Build;
import android.util.Log;

import androidx.annotation.Keep;

/*
 * A helper class to demo that JNI could call into:
 *     private non-static function
 *     public non-static function
 *     static public function
 * The calling code is inside hello-jnicallback.c
 */
public class JniHandler {
    /*
     * Print out status to logcat
     */
    @Keep
    private void updateStatus(String msg) {
        if (msg.toLowerCase().contains("error")) {
            Log.e("JniHandler", "Native Err: " + msg);
        } else {
            Log.i("JniHandler", "Native Msg: " + msg);
        }
    }

    /*
     * Return OS build version: a static function
     */
    @Keep
    static public String getBuildVersion() {
        return Build.VERSION.RELEASE;
    }

    /*
     * Return Java memory info
     */
    @Keep
    public long getRuntimeMemorySize() {
        return Runtime.getRuntime().freeMemory();
    }
}
