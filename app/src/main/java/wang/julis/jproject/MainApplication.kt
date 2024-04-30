package wang.julis.jproject

import android.util.Log
import com.julis.router.Router
import dalvik.system.PathClassLoader
import wang.julis.jwbase.basecompact.NaApplication

/**
 * Created by @juliswang on 2024/04/11 17:15
 *
 * @Description
 */

class MainApplication : NaApplication() {
    override fun onCreate() {
        super.onCreate()
        Router.init(this)
    }

    private fun testClassLoader() {
        val classLoader = applicationContext.classLoader as PathClassLoader
        Log.d(
            "mytest", """
     classLoader : $classLoader
     parent : ${classLoader.parent}
     grandParent : ${classLoader.parent.parent}
     system classloader : ${ClassLoader.getSystemClassLoader()}
     system parent : ${ClassLoader.getSystemClassLoader().parent}
     """.trimIndent()
        )
    }
}

