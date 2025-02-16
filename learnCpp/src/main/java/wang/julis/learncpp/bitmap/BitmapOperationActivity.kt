package wang.julis.learncpp.bitmap

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import android.widget.TextView
import wang.julis.jwbase.utils.ToastUtils
import wang.julis.jwbase.basecompact.BaseActivity
import wang.julis.learncpp.R

/**
 * Created by juliswang on 2022/7/29 17:20
 *
 * Description : jni bitmap相关的操作
 *
 *
 */

class BitmapOperationActivity : BaseActivity() {
    private val mBitmapOps = BitmapOps()
    private lateinit var resultView: ImageView
    private lateinit var mBitmap: Bitmap

    override fun initView() {
        resultView = findViewById(R.id.resultView)
        findViewById<TextView>(R.id.rotate).setOnClickListener {
            rotateBitmap(bitmap = mBitmap)
        }
        findViewById<TextView>(R.id.convert).setOnClickListener {
            convertBitmap(bitmap = mBitmap)
        }
        findViewById<TextView>(R.id.mirror).setOnClickListener {
            mirrorBitmap(bitmap = mBitmap)
        }
    }

    override fun initData() {
        mBitmap = BitmapFactory.decodeResource(resources, R.drawable.julis)
    }

    private fun rotateBitmap(bitmap: Bitmap) {
        val result = mBitmapOps.rotateBitmap(bitmap)
        updateBitmap(result)
    }

    private fun convertBitmap(bitmap: Bitmap) {
        val result = mBitmapOps.convertBitmap(bitmap)
        updateBitmap(result)
    }

    private fun mirrorBitmap(bitmap: Bitmap) {
        val result = mBitmapOps.mirrorBitmap(bitmap)
        updateBitmap(result)
    }

    private fun updateBitmap(bitmap: Bitmap?) {
        mBitmap.recycle()
        if (bitmap == null) {
            ToastUtils.showToast("操作失败")
        }
        resultView.setImageBitmap(bitmap)
        mBitmap = bitmap!!
    }

    override fun getContentView(): Int {
        return R.layout.activity_bitmap_operation
    }


}