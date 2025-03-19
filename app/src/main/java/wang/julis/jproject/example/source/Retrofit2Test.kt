package wang.julis.jproject.example.source

import android.content.Context
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import wang.julis.jproject.example.source.retrofit2.learn.VMNetworkFitTest
import wang.julis.jproject.example.source.retrofit2.repository.HomeRepository
import wang.julis.jproject.example.source.retrofit2.response.BaseResponse
import wang.julis.jproject.example.source.retrofit2.viewmodel.ArticleList
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

        private fun getHomeInfoListNoSuspend() {
            homeRepository.getHomeInfoListNoSuspend(1)?.enqueue(object : Callback<BaseResponse<ArticleList>> {
                override fun onResponse(
                    call: Call<BaseResponse<ArticleList>>,
                    response: Response<BaseResponse<ArticleList>>
                ) {
                    Logger.d("response:${response.body()}")
                }

                override fun onFailure(call: Call<BaseResponse<ArticleList>>, t: Throwable) {
                    Logger.d("response:${t.message}")
                }
            })
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
            getHomeInfoListNoSuspend()
            searchResult()
        }
    }
}