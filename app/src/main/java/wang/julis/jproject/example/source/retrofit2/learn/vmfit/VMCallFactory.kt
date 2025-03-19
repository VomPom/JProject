package wang.julis.jproject.example.source.retrofit2.learn.vmfit

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import wang.julis.jwbase.utils.Logger
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 *
 * Created by @juliswang on 2025/03/18 20:47
 *
 * @Description 使用 Okhttp 进行网络请求
 */
class VMCallFactory {
    private var okHttpClient: OkHttpClient? = null

    private fun okHttpClient(): OkHttpClient {
        if (okHttpClient == null) {
            okHttpClient = OkHttpClient
                .Builder()
                .build()
        }
        return okHttpClient!!
    }

    fun call(request: Request): Call {
        return okHttpClient().newCall(request)
    }
}


/**
 * 代码参考自 [retrofit2.await], 利用 [suspendCancellableCoroutine] 实现方法“同步”调用
 *
 * @receiver Call
 * @return ResponseBody
 */
suspend fun Call.await(): ResponseBody {
    return suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation {
            cancel()
        }
        enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                Logger.e("onFailure e:${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body()
                if (response.isSuccessful) {
                    if (body == null) {
                        continuation.resumeWithException(Throwable("onResponse error..."))
                    } else {
                        continuation.resume(body)
                    }
                } else {
                    continuation.resumeWithException(Exception("response"))
                }
            }
        })
    }
}
