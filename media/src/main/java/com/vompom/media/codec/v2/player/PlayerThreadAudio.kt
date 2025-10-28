package com.vompom.media.codec.v2.player

import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import com.vompom.media.codec.v2.docode.model.PlayerMessage
import com.vompom.media.codec.v2.docode.track.IDecoderTrack


/**
 *
 * Created by @juliswang on 2025/10/14 21:25
 *
 * @Description 播放器的音频线程处理
 */

class PlayerThreadAudio : Handler.Callback {
    private val videoHandler: Handler?
    private val audioDecoderTrack: IDecoderTrack
    private var audioHandler: Handler? = null
    private var nextDecodePosition = 0L

    private var loop = false
    var handlerThread: HandlerThread? = null

    constructor(videoHandler: Handler?, audioDecoderTrack: IDecoderTrack) {
        this.videoHandler = videoHandler
        this.audioDecoderTrack = audioDecoderTrack
        initThread()
    }

    private fun initThread() {
        handlerThread = HandlerThread("PlayerAudioThread")
        handlerThread?.start()
        audioHandler = Handler(handlerThread!!.getLooper(), this)
    }

    override fun handleMessage(msg: Message): Boolean {
        val message = if (msg.obj == null) null else msg.obj as PlayerMessage?
        when (msg.what) {
            PlayerThread.ACTION_PREPARE -> prepare()

            PlayerThread.ACTION_PLAY -> play()

            PlayerThread.ACTION_PAUSE -> pause()

            PlayerThread.ACTION_SEEK -> seek(message?.obj1 as Long)

            PlayerThread.ACTION_STOP -> stop()

            PlayerThread.ACTION_RELEASE -> release()

            PlayerThread.ACTION_READ_SAMPLE -> readSample(message?.obj1 as Long)

        }
        return true
    }

    private fun prepare() {
        audioDecoderTrack.prepare()
    }

    private fun play() {
        loop = true
        // 音频首帧直接seek到目标位置
        seek(0)
        readSample(0)
    }

    private fun pause() {
        loop = false
    }

    private fun stop() {
        loop = false
    }

    private fun release() {
        handlerThread = null
        audioDecoderTrack.release()
    }

    private fun seek(targetUs: Long): Long {
        nextDecodePosition = audioDecoderTrack.seek(targetUs)
        return nextDecodePosition
    }

    private fun readSample(targetTime: Long) {
        audioDecoderTrack.readSample(targetTime)
        nextDecodePosition = playedUs()
        scheduleReadSample()
    }

    private fun scheduleReadSample() {
        if (loop) {
            sendMessageDelay(PlayerThread.ACTION_READ_SAMPLE, nextDecodePosition, 0L)
        }
    }

    fun sendMessage(what: Int, obj: Any? = null) {
        sendMessageDelay(what, obj)
    }

    fun sendMessageDelay(what: Int, obj: Any?, wait: Long = 0L) {
        audioHandler?.let {
            val msg = Message()
            msg.what = what
            msg.obj = PlayerMessage(obj)
            it.sendMessageDelayed(msg, wait)
        }
    }

    fun playedUs(): Long = audioDecoderTrack.playedUs()
}