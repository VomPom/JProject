package com.vompom.sourcecode.retrofit2.learn.vmfit

import kotlinx.coroutines.runBlocking
import okhttp3.Call
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentSkipListMap

/**
 * Created by @juliswang on 2025/03/14 15:59
 *
 * @Description 基于 Retrofit 的设计，实现其核心模块：
 *
 *  - 核心模块
 *      各个 api 接口的动态代理，解析路由以及参数
 *  - 网络请求 Call
 *      使用网络框架进行网络请求
 *  - 数据转化 Converter
 *      负责将 HTTP 响应体转换为 Java 对象，或将 Java 对象序列化为请求体。
 *  其他重要逻辑：
 *      对协程的支持
 */
class VMNetworkFit private constructor(var baseUrl: String) {

    private val methodCache = ConcurrentSkipListMap<String, ApiMethod<*>>()
    private var callFactory = VMCallFactory()
    private var converter = VMConvertorFactory()

    class Builder {
        private var baseUrl: String = ""

        // 各种自定义业务配置 使用 Builder 构建
        fun baseUrl(baseUrl: String): Builder {
            this.baseUrl = baseUrl
            return this
        }

        fun build(): VMNetworkFit = VMNetworkFit(baseUrl)
    }

    /**
     * 生成代理对象
     *
     * @param apiService Class<T>
     * @return T
     */
    fun <T> create(apiService: Class<T>): T =
        Proxy.newProxyInstance(
            apiService.classLoader,
            arrayOf<Class<*>>(apiService)
        ) { proxy, method, args ->
            // 源码里面对 interface default 方法也进行了实现
            loadInvokeMethod(proxy, method).invoke(args)
        } as T


    /**
     * 缓存并加载 API 方法的元信息，以避免重复解析相同的 Method 对象
     * @param proxy Any?
     * @param method Method
     * @return ApiMethod<*>
     */
    private fun loadInvokeMethod(proxy: Any?, method: Method): ApiMethod<*> {
        var apiMethod = methodCache[method.name]
        if (apiMethod != null) {
            return apiMethod
        } else {
            apiMethod = ApiMethod.parseAnnotations<Any>(this, method)
            methodCache[method.name] = apiMethod
        }
        return apiMethod
    }

    abstract class ApiMethod<T> {
        abstract fun invoke(args: Array<out Any>): T

        companion object {
            fun <T> parseAnnotations(vmFit: VMNetworkFit, method: Method): ApiMethod<T> {
                val requestFactory = VMRequestFactory.parseAnnotations<T>(vmFit, method)
                return if (requestFactory.isKotlinSuspendFunction) {
                    SuspendForResponse(vmFit.callFactory, vmFit.converter, method, requestFactory)
                } else {
                    CallAdapted(vmFit.callFactory, vmFit.converter, method, requestFactory)
                }
            }
        }
    }

    abstract class HttpApiMethod<T>(
        private val callFactory: VMCallFactory,
        val converter: VMConvertorFactory,
        val method: Method,
        private val requestFactory: VMRequestFactory
    ) : ApiMethod<T>() {
        override fun invoke(args: Array<out Any>): T {
            val call = callFactory.call(requestFactory.buildRequest(args))
            return adapt(call, args)
        }

        protected abstract fun adapt(call: Call, args: Array<out Any>): T

    }

    /**
     * 适配普通的方法调用
     * @param T
     * @constructor
     */
    class CallAdapted<T>(
        callFactory: VMCallFactory,
        converter: VMConvertorFactory,
        method: Method,
        requestFactory: VMRequestFactory
    ) : HttpApiMethod<T>(callFactory, converter, method, requestFactory) {
        override fun adapt(call: Call, args: Array<out Any>): T {
            return call as T
        }
    }

    /**
     * 适配 kotlin 协程方式调用
     * @param T
     * @constructor
     */
    class SuspendForResponse<T>(
        callFactory: VMCallFactory,
        converter: VMConvertorFactory,
        method: Method,
        requestFactory: VMRequestFactory
    ) : HttpApiMethod<T>(callFactory, converter, method, requestFactory) {
        override fun adapt(call: Call, args: Array<out Any>): T {
            return runBlocking {
                val body = call.await()
                converter.convert(body.string(), method)
            }
        }
    }
}

