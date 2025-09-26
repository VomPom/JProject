package julis.wang.kotlinlearn.feature

import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import julis.wang.kotlinlearn.R
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import wang.julis.jwbase.basecompact.BaseActivity

/**
 *
 * Created by @juliswang on 2025/03/17 10:34
 *
 * @Description 使用协程「消灭」对称式API
 */
class CoroutineDialogActivity : BaseActivity() {
    override fun initView() {
        findViewById<View>(R.id.btn_test).setOnClickListener { v -> showDialog() }
    }

    override fun initData() {

    }

    private fun showDialog() {
        lifecycleScope.launch {
            val result = getUserSelectionByDialog()
            val toastText = if (result) "点击了确认按钮" else "点击了取消按钮"
            Toast.makeText(this@CoroutineDialogActivity, toastText, Toast.LENGTH_SHORT).show()
        }
    }

    override fun getContentViewId(): Int = R.layout.activity_kotlin_test

    /**
     * [suspendCancellableCoroutine] 将回调转换为挂起函数
     *
     * 通过 suspendCancellableCoroutine，可以将基于回调的异步操作封装成一个挂起函数，使其能够以同步的方式编写异步代码。
     * 支持协程取消：suspendCancellableCoroutine 生成的挂起函数是可取消的，这意味着如果协程被取消，相关的回调也会被正确处理，避免资源泄漏。
     * @return Boolean
     */
    private suspend fun getUserSelectionByDialog(): Boolean = suspendCancellableCoroutine { continuation ->
        val dialog = AlertDialog.Builder(this).apply {
            setTitle("title")
            setMessage("这是一个弹窗")
            setPositiveButton("确认") { _, _ ->
                continuation.resumeWith(Result.success(true))
            }
            setNegativeButton("取消") { _, _ ->
                continuation.resumeWith(Result.success(false))
            }
            setOnDismissListener {
                continuation.resumeWith(Result.success(false))
            }
        }.create()

        dialog.show()
        continuation.invokeOnCancellation {
            dialog.dismiss()
        }
    }
}
