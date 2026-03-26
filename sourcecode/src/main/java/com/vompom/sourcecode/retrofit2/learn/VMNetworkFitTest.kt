package com.vompom.sourcecode.retrofit2.learn

import android.content.Context
import com.vompom.sourcecode.retrofit2.learn.vmfit.example.VMHomeRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import wang.julis.jwbase.basecompact.IBaseTest
import wang.julis.jwbase.ext.toJson
import wang.julis.jwbase.utils.Logger
import java.io.IOException

/**
 *
 * Created by @juliswang on 2025/03/19 20:31
 *
 * @Description
 */
@OptIn(DelicateCoroutinesApi::class)
object VMNetworkFitTest : IBaseTest() {
    private val homeRepository by lazy { VMHomeRepository() }

    override fun run(context: Context) {
        suspendGet()
        get()
        post()
    }

    private fun suspendGet() {
        GlobalScope.launch {
            val articleList = homeRepository.getHomeList(1)
            Logger.d("suspendGet:${articleList?.toJson()}")
        }
    }

    private fun post() {
        GlobalScope.launch {
            val articleList = homeRepository.searchResult(1, "compose")
            Logger.d("searchResult post:${articleList?.toJson()}")
        }
    }

    private fun get() {
        homeRepository.getHomeInfoListNoSuspend(1)?.enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                Logger.d("no suspend get response:${response.body()?.string()}")
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                Logger.d("onFailure:${e.message}")
            }

        })
    }
}