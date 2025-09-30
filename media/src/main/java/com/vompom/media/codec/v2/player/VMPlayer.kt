package com.vompom.media.codec.v2.player

import android.view.Surface
import com.vompom.media.codec.v2.docode.AudioDecoder
import com.vompom.media.codec.v2.docode.VideoDecoder

/**
 *
 * Created by @juliswang on 2025/09/25 20:42
 *
 * @Description 基于 [VideoDecoder] [AudioDecoder] 包装播放器，协调整个播放流程，管理播放状态
 *
 */

class VMPlayer : IPlayer {
    private var playerThread: PlayerThread? = null
    private var videoDecoder: VideoDecoder? = null
    private var audioDecoder: AudioDecoder? = null
    private var listener: IPlayer.PlayerListener? = null

    override fun bindPlayer(videoPath: String, surface: Surface) {
        videoDecoder = VideoDecoder(videoPath, surface)
        audioDecoder = AudioDecoder(videoPath)
        playerThread = PlayerThread(
            audioDecoder!!,
            videoDecoder!!
        )

        videoDecoder?.setProgressListener { currentDurationUs: Long, playerDurationUs: Long ->
            listener?.onPositionChanged(currentDurationUs, playerDurationUs)
        }

        playerThread?.sendMessage(PlayerThread.ACTION_PREPARE)
    }

    override fun play() {
        playerThread?.sendMessage(PlayerThread.ACTION_PLAY)
    }

    override fun pause() {
        playerThread?.sendMessage(PlayerThread.ACTION_PAUSE)
    }

    override fun seekTo(positionUs: Long) {
        playerThread?.sendMessage(PlayerThread.ACTION_SEEK, positionUs)
    }

    override fun stop() {
        playerThread?.sendMessage(PlayerThread.ACTION_STOP)
    }

    override fun release() {
        playerThread?.sendMessage(PlayerThread.ACTION_RELEASE)
        playerThread?.release()
    }

    override fun duration(): Long {
        // todo:: 音频跟视频长度不一致的情况
        return videoDecoder?.duration() ?: 0L
    }

    override fun setPlayerListener(listener: IPlayer.PlayerListener) {
        this.listener = listener
    }

}