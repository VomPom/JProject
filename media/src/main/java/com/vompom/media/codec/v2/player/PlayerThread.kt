package com.vompom.media.codec.v2.player

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.vompom.media.codec.v2.docode.model.PlayerMessage
import com.vompom.media.codec.v2.docode.track.IDecoderTrack

/**
 *
 * Created by @juliswang on 2025/09/28 20:52
 *
 * @Description 专门负责协调视频/音频解码器的线程
 */

class PlayerThread {

    companion object {
        const val ACTION_PREPARE: Int = 1

        const val ACTION_PLAY: Int = 2

        const val ACTION_PAUSE: Int = 3

        const val ACTION_STOP: Int = 4

        const val ACTION_SEEK: Int = 5

        const val ACTION_RELEASE: Int = 6

        const val ACTION_QUIT: Int = 7
        const val ACTION_READ_SAMPLE: Int = 9
    }

    var handlerThread: HandlerThread? = null
    var playHandler: Handler? = null

    constructor(player: VMPlayer, videoDecoderTrack: IDecoderTrack, audioDecoderTrack: IDecoderTrack) {
        handlerThread = HandlerThread("PlayerThread")
        handlerThread?.start()

        val messageHandler = PlayerMessageVideoCallback(player, this, videoDecoderTrack)
        playHandler = Handler(handlerThread!!.looper, messageHandler)
        messageHandler.setAudioThread(PlayerThreadAudio(playHandler, audioDecoderTrack))
    }

    fun release() {
        playHandler?.removeCallbacksAndMessages(null)
        playHandler?.sendEmptyMessage(ACTION_RELEASE)
        handlerThread = null
    }

    fun sendMessage(what: Int, obj: Any) {
        playHandler?.let {
            val msg = Message()
            msg.what = what
            msg.obj = PlayerMessage(obj)
            it.sendMessage(msg)
        }
    }

    fun sendMessage(what: Int) {
        sendMessageDelay(what)
    }

    fun sendMessageDelay(what: Int, obj: Any? = null, wait: Long = 0L) {
        playHandler?.let {
            val msg = Message()
            msg.what = what
            msg.obj = PlayerMessage(obj)
            it.sendMessageDelayed(msg, wait)
        }
    }
}