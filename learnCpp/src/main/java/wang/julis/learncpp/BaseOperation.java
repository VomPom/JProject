package wang.julis.learncpp;

import android.util.Log;

/**
 * Created by juliswang on 2022/7/29 17:20
 * <p>
 * Description :
 */
abstract public class BaseOperation {

    static {
        System.loadLibrary("learncpp");
    }

    public abstract void invoke();

    public void print(Object... args) {
        if (args.length == 0) {
            return;
        }

        for (Object arg : args) {
            Log.d("julis", "Java value is " + arg.toString() + "\n");
        }
    }
}
