package julis.wang.kotlinlearn.jetpack

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import julis.wang.kotlinlearn.databinding.ActivityViewModelBinding

/*******************************************************
 *
 * Created by juliswang on 2021/11/02 22:24
 *
 * Description :
 *
 *
 *******************************************************/

class ViewModelTestActivity : AppCompatActivity() {
    private val TAG = "julis.wang"
    private lateinit var model: DataViewModel
    private lateinit var binding: ActivityViewModelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Create a ViewModel the first time the system calls an activity's onCreate() method.
        // Re-created activities receive the same MyViewModel instance created by the first activity.

        // Use the 'by viewModels()' Kotlin property delegate
        // from the activity-ktx artifact
        binding = ActivityViewModelBinding.inflate(layoutInflater)
        setContentView(binding.root)
        model = ViewModelProvider(this, DataViewModelFactory(1)).get(DataViewModel::class.java)
        initView()
        test()
    }


    fun initView() {
        binding.btnTest.setOnClickListener {
            model.update()
        }
        model.count.observe(this) {
            binding.tvData.text = it.toString()
        }
    }

    private fun test() {
        val user = User(1, "juliswang")
        with(user) {
            Log.e(TAG, this.name)
            printUserInfo()
        }.also {
            Log.e(TAG, "With also:$it")
            // it.printUserInfo() wrong
        }
        user.let {
            Log.e(TAG, "Use let:" + it.name)
            it.printUserInfo()
        }
        user.apply {
            Log.e(TAG, "Use apply:" + name)
            printUserInfo()
        }
        user.also {
            Log.e(TAG, "Use also:" + it.name)
            it.printUserInfo()
        }
        user.run {
            Log.e(TAG, "Use run:" + name)
            printUserInfo()
        }
    }


}