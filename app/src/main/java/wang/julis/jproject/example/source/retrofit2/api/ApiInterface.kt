package wang.julis.jproject.example.source.retrofit2.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import wang.julis.jproject.example.source.retrofit2.response.BaseResponse
import wang.julis.jproject.example.source.retrofit2.viewmodel.ArticleList

/**
 * API接口类
 */
interface ApiInterface {
    /**
     * 首页资讯
     * @param page    页码
     * @param pageSize 每页数量
     */
    @GET("/article/list/{page}/json")
    suspend fun getHomeList(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int
    ): BaseResponse<ArticleList>?


    /**
     * 非协程的实现
     *
     * @param page    页码
     * @param pageSize 每页数量
     */
    @GET("/article/list/{page}/json")
    fun getHomeListNoSuspend(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int
    ): Call<ArticleList>?

    /**
     * 搜索结果
     * @param page   页码
     * @param keyWord  关键词，支持多个，空格分开
     */
    @POST("article/query/{page}/json")
    @FormUrlEncoded
    suspend fun searchResult(
        @Path("page") page: Int,
        @Field("k") keyWord: String
    ): BaseResponse<ArticleList>?

}
