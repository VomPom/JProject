package com.vompom.sourcecode.retrofit2.learn.vmfit.example

import com.vompom.sourcecode.retrofit2.repository.BaseRepository
import com.vompom.sourcecode.retrofit2.viewmodel.ArticleList

/**
 * 业务使用的 API
 */
class VMHomeRepository : BaseRepository() {
    /**
     * 首页列表
     * @param page 页码
     * @param page 每页数量
     */
    suspend fun getHomeList(page: Int): ArticleList? {
        return requestResponse {
            VMApiManager.api.getHomeList(page, 20)
        }
    }

    fun getHomeInfoListNoSuspend(page: Int): okhttp3.Call? {
        return VMApiManager.api.getHomeListNoSuspend2(page, 20)
    }

    /**
     * 搜索结果
     * @param page   页码
     * @param keyWord  关键词，支持多个，空格分开
     */
    suspend fun searchResult(page: Int, keyWord: String): ArticleList? {
        return requestResponse {
            VMApiManager.api.searchResult(page, keyWord)
        }
    }

}