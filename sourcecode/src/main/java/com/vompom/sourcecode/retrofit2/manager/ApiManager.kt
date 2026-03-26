package com.vompom.sourcecode.retrofit2.manager

import com.vompom.sourcecode.retrofit2.api.ApiInterface

/**
 * API管理器
 */
object ApiManager {
    val api by lazy { HttpManager.create(ApiInterface::class.java) }
}