package com.vompom.sourcecode.little

import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.vompom.sourcecode.R
import com.vompom.sourcecode.SourceConst
import wang.julis.jwbase.basecompact.BaseActivity

/**
 *
 * Created by @juliswang on 2025/03/20 21:48
 *
 * @Description
 */

class GlideActivity : BaseActivity() {
    override fun initView() {
        findViewById<ImageView>(R.id.iv_pic).apply {
            setOnClickListener { load(SourceConst.Image.random()) }
            performClick()
        }
    }

    override fun initData() {}

    override fun getContentViewId(): Int = R.layout.activity_test

    private fun load(url: String) {
        // glide深入理解它的三部分
        //
        //  with部分，注册编码器，管理请求和生命周期监听
        //
        //  load部分，每个请求单独配置option
        //
        //  into部分，启动请求，加载数据，对数据解码，转码，缓存数据，显示数据
        //
        Thread {
            val glide = Glide.with(this@GlideActivity).load(url)
                .placeholder(R.drawable.ic_share)              // 占位符，异常时显示的图片
                .error(R.drawable.ic_launcher_background)              // 错误时显示的图片
                .skipMemoryCache(false)                    // 启用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)  // 磁盘缓存策略

            Handler(Looper.getMainLooper()).post {
                glide.into(findViewById(R.id.iv_pic))
            }
        }.start()
    }
}









