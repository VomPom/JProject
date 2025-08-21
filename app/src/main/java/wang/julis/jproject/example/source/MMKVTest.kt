package wang.julis.jproject.example.source

import android.content.Context
import com.tencent.mmkv.MMKV
import wang.julis.jwbase.basecompact.IBaseTest

/**
 *
 * Created by @juliswang on 2025/03/25 22:30
 *
 * @Description MMKV 相关的研究
 */
object MMKVTest : IBaseTest() {

    override fun run(context: Context) {
        MMKV.initialize(context)
        val mmkv = MMKV.mmkvWithID("test")
        mmkv.putInt("data", 1)
    }

}