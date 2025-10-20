package com.vompom.media.codec.v2

import android.annotation.SuppressLint
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.SeekBar
import com.vompom.media.R
import com.vompom.media.codec.v2.docode.model.Asset
import com.vompom.media.codec.v2.player.IPlayer
import com.vompom.media.codec.v2.player.VMPlayer
import com.vompom.media.codec.v2.utils.usToS
import com.vompom.media.databinding.ActivityMediaBinding
import com.vompom.media.utils.ResUtils
import wang.julis.jwbase.basecompact.BaseActivity
import wang.julis.jwbase.utils.ThreadUtil

/**
 *
 * Created by @juliswang on 2025/09/25 11:11
 *
 * @Description
 */

class PlayerV2Activity : BaseActivity() {
    private lateinit var binding: ActivityMediaBinding
    private var player: IPlayer = VMPlayer()

    private var surfaceView: SurfaceView? = null

    override fun initView() {
        initPlayer()

        binding.btnPlay.setOnClickListener {
            player.play()
        }
        binding.btnPause.setOnClickListener {
            player.pause()
        }
        binding.btnStop.setOnClickListener {
            player.stop()
        }
        binding.playProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    player.seekTo(seekBar?.progress?.toLong() ?: 0L)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                player.pause()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                player.play()
            }

        })
    }

    override fun initData() {
        player.setPlayerListener(object : IPlayer.PlayerListener {
            @SuppressLint("SetTextI18n")
            override fun onPositionChanged(currentDurationUs: Long, playerDurationUs: Long) {
                ThreadUtil.runOnMain {
                    binding.tvTime.text =
                        "${usToS(currentDurationUs)} / ${usToS(playerDurationUs)}".toString()
                    binding.playProgress.progress = currentDurationUs.toInt()
                    binding.playProgress.max = playerDurationUs.toInt()
                }
            }

        })
    }

    override fun getContentView(): View {
        binding = ActivityMediaBinding.inflate(layoutInflater)
        return binding.root
    }

    private fun initPlayer() {
        surfaceView = findViewById<SurfaceView?>(R.id.sv_video)
        surfaceView!!.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                player.bindPlayer(
                    listOf(
                        Asset(ResUtils.testHok),
                        Asset(ResUtils.video10s),
                    ), holder.surface
                )
                player.play()
            }

            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
            }
        })
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onResume() {
        super.onResume()
        player.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}
