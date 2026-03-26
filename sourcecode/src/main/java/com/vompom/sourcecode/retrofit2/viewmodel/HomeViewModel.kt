package com.vompom.sourcecode.retrofit2.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.vompom.sourcecode.retrofit2.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * ViewModel 实现
 */
class HomeViewModel : ViewModel() {

    private val repository by lazy { HomeRepository() }
    private val searchResultLiveData = MutableLiveData<MutableList<ArticleInfo>?>()

    /**
     * 首页列表
     * @param page 页码
     */
    fun getHomeInfoList(page: Int): LiveData<ArticleList> {
        return liveData {
            val response = safeApiCall(errorBlock = { code, errorMsg ->
//                TipsToast.showTips(errorMsg)
            }) {
                repository.getHomeInfoList(page)
            }
            response?.let {
                emit(it)
            }
        }
    }

    /**
     * 搜索结果
     * @param page   页码
     * @param keyWord  关键词，支持多个，空格分开
     */
    fun searchResult(
        page: Int,
        keyWord: String
    ): LiveData<MutableList<ArticleInfo>?> {
        launchUI(errorBlock = { code, error ->
//            TipsToast.showTips(error)
            searchResultLiveData.value = null
        }, responseBlock = {
            val data = repository.searchResult(page, keyWord)
            searchResultLiveData.value = data?.datas
        })
        return searchResultLiveData
    }

    /**
     * 运行在主线程中，可直接调用
     * @param errorBlock 错误回调
     * @param responseBlock 请求函数
     */
    fun launchUI(errorBlock: (Int?, String?) -> Unit, responseBlock: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            safeApiCall(errorBlock = errorBlock, responseBlock)
        }
    }


    /**
     * 需要运行在协程作用域中
     * @param errorBlock 错误回调
     * @param responseBlock 请求函数
     */
    suspend fun <T> safeApiCall(
        errorBlock: suspend (Int?, String?) -> Unit,
        responseBlock: suspend () -> T?
    ): T? {
        try {
            return responseBlock()
        } catch (e: Exception) {
            e.printStackTrace()
//            val exception = ExceptionHandler.handleException(e)
//            errorBlock(exception.errCode, exception.errMsg)
        }
        return null
    }

}