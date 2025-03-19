package wang.julis.jproject.example.source.retrofit2.learn.vmfit

import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.Request
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import kotlin.coroutines.Continuation

/**
 *
 * Created by @juliswang on 2025/03/18 20:37
 *
 * @Description 原 retrofit 采用 Builder 模式实现，这里简易实现
 */
class VMRequestFactory(
    private val vmFit: VMNetworkFit,
    private val methodAnnotations: Array<Annotation>,
    private val parameterAnnotationsArray: Array<Array<Annotation>>,
) {

    var isKotlinSuspendFunction = true          // 是否为协程调用方法
    private var isFormUrlEncoded = false
    private var fieldMap: MutableMap<String, String> = mutableMapOf()

    companion object {
        fun <T> parseAnnotations(vmFit: VMNetworkFit, method: Method): VMRequestFactory {
            val methodAnnotations = method.annotations
            val parameterAnnotations = method.parameterAnnotations

            return VMRequestFactory(vmFit, methodAnnotations, parameterAnnotations).apply {
                isKotlinSuspendFunction = checkIsSuspendMethod(method)
            }
        }

        private fun checkIsSuspendMethod(method: Method): Boolean {
            val lastParameterizedType = method.genericParameterTypes.last()
            if (lastParameterizedType is ParameterizedType) {
                if (lastParameterizedType.rawType == Continuation::class.java) {
                    return true
                }
            }
            return false
        }
    }

    private fun parseMethodAnnotation(annotations: Array<Annotation>): Pair<HttpMethod, String> {
        // 这里只处理 GET/POST 方法，其他注解（例如Header）暂不处理
        var httpMethod = Pair(HttpMethod.GET, "")
        for (annotation in annotations) {
            when (annotation) {
                is GET -> httpMethod = Pair(HttpMethod.GET, annotation.value)
                is POST -> httpMethod = Pair(HttpMethod.POST, annotation.value)
                is FormUrlEncoded -> isFormUrlEncoded = true
                else -> TODO("parseMethodAnnotation others.")
            }
        }
        return httpMethod
    }

    private fun parseParameterAnnotation(
        parameterAnnotationsArray: Array<Array<Annotation>>,
        path: String,
        args: Array<out Any>
    ): String {
        var parameterCount = parameterAnnotationsArray.size
        if (isKotlinSuspendFunction) {
            parameterCount--
        }
        var newPath = path
        for (i in 0 until parameterCount) {
            parameterAnnotationsArray[i].forEach { annotation ->
                newPath = simpleChangeUrl(annotation, newPath, args[i])
            }
        }
        return if (newPath.startsWith("/")) newPath else "/".plus(newPath)
    }

    private fun simpleChangeUrl(annotation: Annotation, path: String, arg: Any): String {
        var newPath = path
        val argStr = arg.toString()
        when (annotation) {
            is Path -> {
                newPath = newPath.replace("{${annotation.value}}", argStr)
            }

            is Query -> {
                val value = annotation.value
                newPath += if (!newPath.contains("?")) {
                    "?$value=$argStr"
                } else {
                    "&$value=$argStr"
                }
            }

            is Field -> {
                fieldMap[annotation.value] = argStr
            }

            else -> {
                //no-op
            }
        }
        return newPath
    }

    fun buildRequest(args: Array<out Any>): Request {
        val (httpMethod, annotationPath) = parseMethodAnnotation(methodAnnotations)
        val pathAndQuery = parseParameterAnnotation(parameterAnnotationsArray, annotationPath, args)
        val finalUrl = (vmFit.baseUrl + pathAndQuery).replace("//", "/")

        // no headers.
        val headers = Headers.Builder().build()

        var builder = Request
            .Builder()
            .headers(headers)

        builder = when (httpMethod) {
            HttpMethod.GET -> {
                builder.get()
            }

            HttpMethod.POST -> {
                post(builder)
            }

        }
        return builder.url(finalUrl).build()
    }

    private fun post(builder: Request.Builder): Request.Builder {
        var formBuilder = FormBody.Builder()
        fieldMap.forEach {
            formBuilder = formBuilder.add(it.key, it.value)
        }
        return builder.post(formBuilder.build())
    }

    enum class HttpMethod {
        POST,
        GET,
        //...
    }
}