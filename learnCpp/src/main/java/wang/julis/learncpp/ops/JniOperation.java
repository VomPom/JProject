package wang.julis.learncpp.ops;

import android.util.Log;

import wang.julis.learncpp.BaseOperation;

/**
 * Created by juliswang on 2022/8/2 20:35
 * <p>
 * Description :
 */

public class JniOperation extends BaseOperation {

    @Override
    public void invoke() {
        Log.e(TAG, hellJni());
    }

    public native String hellJni();
}
