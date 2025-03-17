package wang.julis.jproject.example.source.retrofit2.interceptor


import okhttp3.Interceptor
import okhttp3.Response
import wang.julis.jproject.example.source.retrofit2.manager.CookiesManager
import wang.julis.learncpp.common.LogUtil

/**
 * 头信息拦截器
 * 添加头信息
 */
class HeaderInterceptor : Interceptor {
    companion object {
        const val KEY_COOKIE = "Cookie"
        const val COLLECTION_WEBSITE = "lg/collect"
        const val NOT_COLLECTION_WEBSITE = "lg/uncollect"
        const val ARTICLE_WEBSITE = "article"
        const val COIN_WEBSITE = "lg/coin"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newBuilder = request.newBuilder()
        newBuilder.addHeader("Content-type", "application/json; charset=utf-8")

        val host = request.url().host()
        val url = request.url().toString()

        //给有需要的接口添加Cookies
        if (!host.isNullOrEmpty() && (url.contains(COLLECTION_WEBSITE)
                    || url.contains(NOT_COLLECTION_WEBSITE)
                    || url.contains(ARTICLE_WEBSITE)
                    || url.contains(COIN_WEBSITE))
        ) {
            val cookies = CookiesManager.getCookies()
            LogUtil.e("HeaderInterceptor:cookies:$cookies")
            if (!cookies.isNullOrEmpty()) {
                newBuilder.addHeader(KEY_COOKIE, cookies)
            }
        }
        return chain.proceed(newBuilder.build())
    }
}