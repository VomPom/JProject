package wang.julis.jproject.example.source.retrofit2.manager

import wang.julis.jproject.example.source.retrofit2.api.ApiInterface

/**
 * API管理器
 */
object ApiManager {
    val api by lazy { HttpManager.create(ApiInterface::class.java) }
}