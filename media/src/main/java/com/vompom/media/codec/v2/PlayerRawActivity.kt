package com.vompom.media.codec.v2

import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import com.vompom.media.R
import com.vompom.media.codec.v2.docode.AudioDecoder
import com.vompom.media.codec.v2.docode.VideoDecoder
import com.vompom.media.databinding.ActivityMediaBinding
import com.vompom.media.utils.ResUtils
import wang.julis.jwbase.basecompact.BaseActivity

/**
 *
 * Created by @juliswang on 2025/09/25 11:11
 *
 * @Description
 */

class PlayerRawActivity : BaseActivity() {
    private lateinit var binding: ActivityMediaBinding
    private lateinit var videoDecoder: VideoDecoder
    private lateinit var audioDecoder: AudioDecoder

    private var surfaceView: SurfaceView? = null

    override fun initView() {
        initPlayer()

        binding.btnPlay.setOnClickListener {
            videoDecoder.play()
            audioDecoder.play()
        }
        binding.btnPause.setOnClickListener {
            videoDecoder.pause()
            audioDecoder.pause()
        }
        binding.btnStop.setOnClickListener {
            videoDecoder.stop()
            audioDecoder.stop()
        }
    }

    override fun initData() {

    }

    override fun getContentView(): View {
        binding = ActivityMediaBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun initPlayer() {
        surfaceView = findViewById<SurfaceView?>(R.id.sv_video)
        surfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                videoDecoder = VideoDecoder(ResUtils.video10s, holder.surface)
                videoDecoder.prepare()
                audioDecoder = AudioDecoder(ResUtils.video10s)
                audioDecoder.prepare()
                Thread(videoDecoder).start()
                Thread(audioDecoder).start()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        videoDecoder.stop()
        videoDecoder.release()

        audioDecoder.stop()
        audioDecoder.release()
    }
}
