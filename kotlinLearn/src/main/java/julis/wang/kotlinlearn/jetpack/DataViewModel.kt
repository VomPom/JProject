package julis.wang.kotlinlearn.jetpack

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/*******************************************************
 *
 * Created by juliswang on 2021/11/02 22:43
 *
 * Description :
 *
 *
 *******************************************************/

class DataViewModel(initDataValue: Int) : ViewModel() {
    val count: LiveData<Int>
        get() = _count

    private val _count = MutableLiveData<Int>()

    init {
        _count.value = initDataValue
    }

//    private val users: MutableLiveData<List<User>> by lazy {
//        MutableLiveData<List<User>>().also {
//            loadUsers()
//        }
//    }
//
//    fun getUsers(): LiveData<List<User>> {
//        return users
//    }
//
//    private fun loadUsers() {
//        // Do an asynchronous operation to fetch users.
//    }

    fun update() {
        val count = count.value ?: 0
        this._count.value = count + 1;
    }

    override fun onCleared() {
        Log.e("julis", "DataViewModel 数据被销毁～")
        super.onCleared()
    }
}