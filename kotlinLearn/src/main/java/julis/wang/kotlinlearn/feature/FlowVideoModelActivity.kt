package julis.wang.kotlinlearn.feature

import android.widget.Button
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import julis.wang.kotlinlearn.R
import julis.wang.kotlinlearn.jetpack.DataRepository
import julis.wang.kotlinlearn.jetpack.FlowViewModel
import julis.wang.kotlinlearn.jetpack.UiState
import kotlinx.coroutines.launch
import wang.julis.jwbase.basecompact.BaseActivity
import wang.julis.jwbase.utils.Logger

/**
 *
 * Created by @juliswang on 2025/03/07 10:37
 *
 * @Description
 */

class FlowVideoModelActivity : BaseActivity() {
    private val viewModel: FlowViewModel by viewModels { MyViewModelFactory() }
    override fun initView() {
        findViewById<Button>(R.id.btn_start).setOnClickListener {
            viewModel.fetchData()
        }

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is UiState.Loading -> Logger.d("Loading status...")
                    is UiState.Success -> Logger.d("Success status...")
                    is UiState.Error -> Logger.d("Error status...")
                }
            }
        }
    }

    override fun initData() {

    }

    override fun getContentView(): Int {
        return R.layout.activity_kotlin_test
    }

    class MyViewModelFactory() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return modelClass.getConstructor(DataRepository::class.java).newInstance(DataRepository())
        }
    }
}

















