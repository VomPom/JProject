package com.vompom.media.codec.v2.player

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.vompom.media.codec.v2.docode.IDecoder

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
    }

    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null

    constructor(audioDecoder: IDecoder, videoDecoder: IDecoder) {
        handlerThread = HandlerThread("PlayerThread")
        handlerThread?.start()

        val messageHandler = PlayerMessageHandler(audioDecoder, videoDecoder)
        handler = Handler(handlerThread!!.looper, messageHandler)
    }

    fun release() {
        handlerThread?.quitSafely()
        handlerThread = null
    }

    fun sendMessage(what: Int, obj: Any) {
        handler?.let {
            val msg = Message()
            msg.what = what
            msg.obj = obj
            it.sendMessage(msg)
        }
    }

    fun sendMessage(what: Int) {
        handler?.let {
            val msg = Message()
            msg.what = what
            it.sendMessage(msg)
        }
    }
}