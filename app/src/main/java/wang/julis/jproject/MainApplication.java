package wang.julis.jproject;

import android.util.Log;

import dalvik.system.PathClassLoader;
import wang.julis.jwbase.basecompact.NaApplication;

/*******************************************************
 *
 * Created by julis.wang on 2021/06/08 15:28
 *
 * Description :
 *
 * History   :
 *
 *******************************************************/

public class MainApplication extends NaApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        test();
    }

    private void test() {
        PathClassLoader classLoader = (PathClassLoader) getApplicationContext().getClassLoader();
        Log.d("mytest", "classLoader : " + classLoader + "\n" +
                "parent : " + classLoader.getParent() + "\n" +
                "grandParent : " + classLoader.getParent().getParent() + "\n" +
                "system classloader : " + ClassLoader.getSystemClassLoader() + "\n" +
                "system parent : " + ClassLoader.getSystemClassLoader().getParent());
    }
}
