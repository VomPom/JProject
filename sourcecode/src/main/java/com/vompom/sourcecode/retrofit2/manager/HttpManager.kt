package com.vompom.sourcecode.retrofit2.manager

import android.util.Log
import com.vompom.sourcecode.retrofit2.interceptor.CookiesInterceptor
import com.vompom.sourcecode.retrofit2.interceptor.HeaderInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * 网络请求管理类
 */
object HttpManager {
    private const val BASE_URL = "https://www.wanandroid.com"
    private val mRetrofit: Retrofit

    init {
        mRetrofit = Retrofit.Builder()
            .client(initOkHttpClient())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 获取 apiService
     */
    fun <T> create(apiService: Class<T>): T {
        return mRetrofit.create(apiService)
    }

    /**
     * 初始化OkHttp
     */
    private fun initOkHttpClient(): OkHttpClient {
        val build = OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .writeTimeout(12, TimeUnit.SECONDS)
            .readTimeout(12, TimeUnit.SECONDS)

        build.addInterceptor(CookiesInterceptor())
        build.addInterceptor(HeaderInterceptor())

        //日志拦截器
        val logInterceptor = HttpLoggingInterceptor { message: String ->
            Log.i("okhttp", "data:$message")
        }
//        if (SumAppHelper.isDebug()) {
//            logInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        } else {
//            logInterceptor.level = HttpLoggingInterceptor.Level.BASIC
//        }
        build.addInterceptor(logInterceptor)
        //网络状态拦截
        build.addInterceptor { chain -> chain.proceed(chain.request()) }
        return build.build()
    }
}