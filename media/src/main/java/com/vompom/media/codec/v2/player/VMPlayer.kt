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
import com.vompom.media.codec.v2.utils.sToUs

/**
 *
 * Created by @juliswang on 2025/09/25 20:42
 *
 * @Description 基于 [VideoDecoder] [AudioDecoder] 包装播放器，协调整个播放流程，管理播放状态
 *
 */

class VMPlayer : IPlayer, Handler.Callback {
    private var playerThread: PlayerThread? = null
    private var playListener: IPlayer.PlayerListener? = null
    var mMainHandler: Handler = Handler(Looper.getMainLooper(), this)

    private var loop = true
    private var playUs: Long = 0L

    companion object {
        const val TYPE_STATES: Int = 1
        const val TYPE_PROGRESS: Int = 2
        const val TYPE_VIEWPORT_UPDATE: Int = 3
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
        return sToUs(6)
    }

    override fun setLoop(loop: Boolean) {
        this.loop = loop
    }

    override fun setPlayerListener(listener: IPlayer.PlayerListener) {
        this.playListener = listener
    }

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            TYPE_PROGRESS -> {
                playUs = msg.obj as Long
                if (mMainHandler.hasMessages(TYPE_PROGRESS) == false) {
                    playListener?.onPositionChanged(playUs, duration())
                }
            }
        }
        return false
    }

}