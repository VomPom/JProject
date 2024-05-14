package julis.wang.kotlinlearn.feature

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import julis.wang.kotlinlearn.databinding.ActivityKotlinTestBinding
import julis.wang.kotlinlearn.extension.intentData
import julis.wang.kotlinlearn.extension.viewBinding

/**
 *
 * Created by @juliswang on 2024/05/13 14:23
 *
 * @Description
 */
class IntentExtActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityKotlinTestBinding::inflate, true)

    companion object {
        const val TAG = "IntentExtActivity"
    }

    private val intData2: Int? by lazy {
        intent?.getIntExtra("int", 5)
    }

    private val intData: Int by intentData("int", 0)
    private val floatData: Float by intentData("float", 0.0f)
    private val doubleData: Double by intentData("double", 0.0)
    private val longData: Long by intentData("long", 0L)
    private val stringData: String? by intentData("string")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.btnStart.setOnClickListener {
            jumpWithData()
        }
        printData()
    }

    private fun printData() {
        Log.e(TAG, "int data:$intData")
        Log.e(TAG, "float data:$floatData")
        Log.e(TAG, "double data:$doubleData")
        Log.e(TAG, "long data:$longData")
        Log.e(TAG, "string data:$stringData")
        Log.e(TAG, "intData2 data:$intData2")
    }

    private fun jumpWithData() {
        val intent = Intent(this, IntentExtActivity::class.java).apply {
            putExtra("int", 1)
            putExtra("float", 1.0f)
            putExtra("double", 1.0)
            putExtra("long", 1L)
            putExtra("string", "string data")
        }
        startActivity(intent)
    }
}