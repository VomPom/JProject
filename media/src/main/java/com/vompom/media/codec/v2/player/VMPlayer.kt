package com.vompom.media.codec.v2.player

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Surface
import com.vompom.media.codec.v2.docode.decorder.AudioDecoder
import com.vompom.media.codec.v2.docode.decorder.VideoDecoder
import com.vompom.media.codec.v2.docode.model.ClipAsset
import com.vompom.media.codec.v2.docode.model.TrackSegment
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
    private var playListener: IPlayer.PlayerListener? = null
    private var segments: List<TrackSegment> = emptyList()
    private var durationUs: Long = -1L
    var mMainHandler: Handler = Handler(Looper.getMainLooper(), this)

    private var loop = true
    private var playUs: Long = 0L

    companion object {
        var currentPlayer: VMPlayer? = null
        const val TYPE_STATES: Int = 1
        const val TYPE_PROGRESS: Int = 2
        const val TYPE_VIEWPORT_UPDATE: Int = 3

        fun create(): VMPlayer {
            currentPlayer = VMPlayer()
            return currentPlayer!!
        }
    }

    private constructor()

    override fun bindPlayer(assets: List<ClipAsset>, surface: Surface) {
        segments = createTrackSegments(assets)

        playerThread = PlayerThread(
            this,
            VideoDecoderTrack(segments, surface),
            AudioDecoderTrack(segments)
        )
        playerThread?.sendMessage(PlayerThread.ACTION_PREPARE)
    }

    private fun createTrackSegments(assets: List<ClipAsset>): List<TrackSegment> {
        var preDurationUs = 0L
        val trackSegmentList = assets.map {
            val segment = TrackSegment(it)
            segment.timelineRange.updateStartUs(preDurationUs)
            preDurationUs += segment.timelineRange.durationUs
            segment
        }
        return trackSegmentList
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
        playerThread?.release()
    }

    override fun duration(): Long {
        if (durationUs == -1L) {
            durationUs = segments.sumOf {
                it.timelineRange.durationUs
            }
        }
        return durationUs
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