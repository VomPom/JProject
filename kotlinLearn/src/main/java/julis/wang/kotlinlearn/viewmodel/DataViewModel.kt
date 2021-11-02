package julis.wang.kotlinlearn.viewmodel

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

class DataViewModel : ViewModel() {

    private val users: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>().also {
            loadUsers()
        }
    }

    fun getUsers(): LiveData<List<User>> {
        return users
    }

    private fun loadUsers() {
        // Do an asynchronous operation to fetch users.
    }
}