package julis.wang.kotlinlearn.feature

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import julis.wang.kotlinlearn.databinding.ActivityKotlinTestBinding
import julis.wang.kotlinlearn.extension.viewBinding
import wang.julis.jwbase.utils.ToastUtils

/**
 *
 * Created by @juliswang on 2024/05/13 14:23
 *
 * @Description
 */
class ViewBindingActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityKotlinTestBinding::inflate, true)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnStart.setOnClickListener {
            ToastUtils.showToast("view binding success.")
        }
    }
}