package com.vompom.media.codec.v1.player

import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.TextureView
import android.view.TextureView.SurfaceTextureListener
import android.widget.Button
import com.vompom.media.R
import com.vompom.media.utils.ResUtils
import wang.julis.jwbase.basecompact.BaseActivity
import wang.julis.jwbase.utils.Logger
import java.io.File

/**
 *
 * Created by @juliswang on 2025/09/16 19:20
 *
 * @Description 基于 MediaExtractor 提取视频数据并将其送到 MediaCodec 进行编码并展示在 TextureView 上
 */

class PlayWithoutAudioActivity : BaseActivity(), SurfaceTextureListener {
    private lateinit var textureView: TextureView
    private var playTask: PlayerWithoutAudio.PlayTask? = null

    override fun initView() {
        findViewById<Button>(R.id.btn_play).setOnClickListener {
            playTask?.execute()
        }
        textureView = findViewById<TextureView>(R.id.texture_video)
        textureView.surfaceTextureListener = this
    }

    override fun initData() {}

    override fun getContentViewId(): Int = R.layout.activity_textureview_player

    override fun onSurfaceTextureAvailable(
        surface: SurfaceTexture,
        width: Int,
        height: Int
    ) {
        val videoFile = File(ResUtils.testWz)
        val moviePlayer = PlayerWithoutAudio(videoFile, Surface(surface), object : PlayerWithoutAudio.FrameCallback {
            override fun preRender(presentationTimeUsec: Long) {
                Logger.d("play:${presentationTimeUsec / 1000 / 1000.0f}s")
                // 实际上由于解码的速度大于 Buffer 消耗的速度，需要控制其 output 的速度，从而使得流畅播放
                Thread.sleep(30)
            }

            override fun postRender() {
            }

            override fun loopReset() {
                Logger.d("play reset.")
            }
        })
        playTask = PlayerWithoutAudio.PlayTask(moviePlayer, object : PlayerWithoutAudio.PlayerFeedback {
            override fun playbackStopped() {
                Logger.d("play complete.")
            }

        })
        playTask?.execute()
    }

    override fun onSurfaceTextureSizeChanged(
        surface: SurfaceTexture,
        width: Int,
        height: Int
    ) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
}