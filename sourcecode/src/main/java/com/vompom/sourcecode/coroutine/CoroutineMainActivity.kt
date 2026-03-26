package com.vompom.sourcecode.coroutine

import com.vompom.sourcecode.retrofit2.Retrofit2Test
import kotlinx.coroutines.channels.Channel
import wang.julis.jwbase.basecompact.baseList.BaseListActivity

/**
 *
 * Created by @juliswang on 2026/03/19 19:58
 *
 * @Description
 */
class CoroutineMainActivity : BaseListActivity() {
    override fun initData() {
        addItem("Retrofit2") { Retrofit2Test.run(this) }

    }
    private suspend fun suspendGet() {
        Channel<String>().send("1")
    }
}