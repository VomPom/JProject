package julis.wang.kotlinlearn.viewmodel

import android.os.Bundle
import androidx.lifecycle.Observer
import julis.wang.kotlinlearn.R
import wang.julis.jwbase.basecompact.BaseActivity

/*******************************************************
 *
 * Created by juliswang on 2021/11/02 22:24
 *
 * Description :
 *
 *
 *******************************************************/

class ViewModelTestActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activities receive the same MyViewModel instance created by the first activity.
        // Use the 'by viewModels()' Kotlin property delegate
        // from the activity-ktx artifact
        val model: DataViewModel by viewModels()
        model.getUsers().observe(this, Observer<List<User>> { users ->
            // update UI
        })
    }

    override fun initView() {
        TODO("Not yet implemented")
    }

    override fun initData() {
        TODO("Not yet implemented")
    }

    override fun getContentView(): Int {
        return R.layout.activity_test
    }


}