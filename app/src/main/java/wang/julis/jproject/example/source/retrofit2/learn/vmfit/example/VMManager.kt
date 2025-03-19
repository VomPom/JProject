package wang.julis.jproject.example.source.retrofit2.learn.vmfit.example

import wang.julis.jproject.example.source.retrofit2.learn.vmfit.VMNetworkFit

/**
 *
 * Created by @juliswang on 2025/03/14 16:01
 *
 * @Description 整个对外暴露的
 */
object VMManager {
    private val vmRequest: VMNetworkFit

    init {
        vmRequest = VMNetworkFit.Builder()
            .baseUrl("https://www.wanandroid.com")
            .build()
    }

    fun <T> create(clazz: Class<T>): T {
        return vmRequest.create(clazz)
    }

}