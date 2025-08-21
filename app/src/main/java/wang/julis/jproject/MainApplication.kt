package wang.julis.jproject

import com.julis.router.Router
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

}

