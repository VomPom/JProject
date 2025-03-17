package wang.julis.jproject.example.source

import android.content.Context
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import wang.julis.jproject.example.source.retrofit2.learn.my.VMHomeRepository
import wang.julis.jproject.example.source.retrofit2.repository.HomeRepository
import wang.julis.jwbase.basecompact.IBaseTest
import wang.julis.jwbase.ext.toJson
import wang.julis.jwbase.utils.Logger

/**
 *
 * Created by @juliswang on 2025/03/13 10:30
 *
 * @Description Retrofit2 相关的研究
 */
object Retrofit2Test : IBaseTest() {
    private val homeRepository by lazy { HomeRepository() }

    override fun run(context: Context) {
//        RetroFitTest.run(context)      // 一个GET 和一个POST 请求的用法
//        DynamicDelegate.run(context)   // 学习关于“动态代理”的实现
        VMNetworkFitTest.run(context)  // 手写“Retrofit”的核心实现
    }

    object RetroFitTest : IBaseTest() {
        @OptIn(DelicateCoroutinesApi::class)
        private fun getHomeInfoList() {
            GlobalScope.launch {
                val articleList = homeRepository.getHomeInfoList(1)
                Logger.d("articleList:${articleList?.toJson()}")
            }
        }

        @OptIn(DelicateCoroutinesApi::class)
        private fun searchResult() {
            GlobalScope.launch {
                val articleList = homeRepository.searchResult(1, "compose")
                Logger.d("searchResult:${articleList?.toJson()}")
            }
        }

        override fun run(context: Context) {
            getHomeInfoList()
            searchResult()
        }
    }

    object VMNetworkFitTest : IBaseTest() {
        private val homeRepository by lazy { VMHomeRepository() }

        override fun run(context: Context) {
            GlobalScope.launch {
                homeRepository.getHomeList(1)
            }
        }

    }
}