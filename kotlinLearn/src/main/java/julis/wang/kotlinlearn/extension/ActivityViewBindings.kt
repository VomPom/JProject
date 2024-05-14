package julis.wang.kotlinlearn.extension

import android.view.LayoutInflater
import androidx.core.app.ComponentActivity
import androidx.viewbinding.ViewBinding

/**
 *
 * Created by @juliswang on 2024/05/13 14:25
 *
 * @Description
 */
inline fun <T : ViewBinding> ComponentActivity.viewBinding(
    crossinline bindingInflater: (LayoutInflater) -> T,
    setContentView: Boolean = false
) = lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
    val binding = bindingInflater.invoke(layoutInflater)
    if (setContentView) {
        setContentView(binding.root)
    }
    binding
}
