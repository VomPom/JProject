package julis.wang.kotlinlearn.jetpack

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/*******************************************************
 *
 * Created by juliswang on 2021/11/03 22:55
 *
 * Description :
 *
 *
 *******************************************************/

class DataViewModelFactory(private val initData: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DataViewModel(initData) as T
    }


}