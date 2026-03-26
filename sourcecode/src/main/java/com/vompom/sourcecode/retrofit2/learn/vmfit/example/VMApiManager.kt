package com.vompom.sourcecode.retrofit2.learn.vmfit.example

import com.vompom.sourcecode.retrofit2.api.ApiInterface

/**
 * API管理器
 */
object VMApiManager {
    val api by lazy { VMManager.create(ApiInterface::class.java) }
}