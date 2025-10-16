package com.vompom.media.codec.v2.player

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Surface
import com.vompom.media.codec.v2.docode.TrackSegment
import com.vompom.media.codec.v2.docode.decorder.AudioDecoder
import com.vompom.media.codec.v2.docode.decorder.VideoDecoder
import com.vompom.media.codec.v2.docode.track.AudioDecoderTrack
import com.vompom.media.codec.v2.docode.track.VideoDecoderTrack

/**
 *
 * Created by @juliswang on 2025/09/25 20:42
 *
 * @Description 基于 [VideoDecoder] [AudioDecoder] 包装播放器，协调整个播放流程，管理播放状态
 *
 */

class VMPlayer : IPlayer, Handler.Callback {
    private var playerThread: PlayerThread? = null
    private var listener: IPlayer.PlayerListener? = null
    private var mMainHandler: Handler? = null
    private var loop = true

    constructor() {
        mMainHandler = Handler(Looper.getMainLooper(), this)
    }

    override fun bindPlayer(videoList: List<String>, surface: Surface) {
        val trackSegmentList = videoList.map { TrackSegment(it) }
        val videoDecoderTrack = VideoDecoderTrack(trackSegmentList, surface)
        val audioDecoderTrack = AudioDecoderTrack(trackSegmentList)

        playerThread = PlayerThread(
            this,
            videoDecoderTrack,
            audioDecoderTrack
        )
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
        return 0L // videoDecoder?.duration() ?: 0L
    }

    override fun setLoop(loop: Boolean) {
        this.loop = loop
    }

    override fun setPlayerListener(listener: IPlayer.PlayerListener) {
        this.listener = listener
    }

    override fun handleMessage(msg: Message): Boolean {
        TODO("Not yet implemented")
    }

}