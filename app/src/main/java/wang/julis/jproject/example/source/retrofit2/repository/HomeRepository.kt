package wang.julis.jproject.example.source.retrofit2.repository


import retrofit2.Call
import wang.julis.jproject.example.source.retrofit2.manager.ApiManager
import wang.julis.jproject.example.source.retrofit2.response.BaseResponse
import wang.julis.jproject.example.source.retrofit2.viewmodel.ArticleList

/**
 *首页请求仓库
 */
class HomeRepository : BaseRepository() {
    /**
     * 首页列表
     * @param page 页码
     * @param page 每页数量
     */
    suspend fun getHomeInfoList(page: Int): ArticleList? {
        return requestResponse {
            ApiManager.api.getHomeList(page, 20)
        }
    }

    fun getHomeInfoListNoSuspend(page: Int): Call<BaseResponse<ArticleList>>? {
        return ApiManager.api.getHomeListNoSuspend(page, 20)
    }


    /**
     * 搜索结果
     * @param page   页码
     * @param keyWord  关键词，支持多个，空格分开
     */
    suspend fun searchResult(page: Int, keyWord: String): ArticleList? {
        return requestResponse {
            ApiManager.api.searchResult(page, keyWord)
        }
    }

}