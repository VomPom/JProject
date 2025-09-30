package com.vompom.media.codec.v2.player

import android.os.Handler
import android.os.Message
import com.vompom.media.codec.v2.docode.IDecoder

/**
 *
 * Created by @juliswang on 2025/09/28 21:20
 *
 * @Description
 */

class PlayerMessageHandler(val audioDecoder: IDecoder, val videoDecoder: IDecoder) : Handler.Callback {

    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            PlayerThread.ACTION_PREPARE -> prepare()

            PlayerThread.ACTION_PLAY -> play()

            PlayerThread.ACTION_PAUSE -> pause()

            PlayerThread.ACTION_SEEK -> seek(msg.obj as Long)

            PlayerThread.ACTION_STOP -> stop()

            PlayerThread.ACTION_RELEASE -> release()

        }
        return true
    }

    private fun prepare() {
        audioDecoder.prepare()
        videoDecoder.prepare()

        Thread(audioDecoder).start()
        Thread(videoDecoder).start()
    }

    private fun play() {
        audioDecoder.play()
        videoDecoder.play()
    }

    private fun pause() {
        audioDecoder.pause()
        videoDecoder.pause()
    }

    private fun stop() {
        audioDecoder.stop()
        videoDecoder.stop()
    }

    private fun release() {
        audioDecoder.release()
        videoDecoder.release()
    }

    private fun seek(positionUs: Long) {
        audioDecoder.seek(positionUs)
        videoDecoder.seek(positionUs)
    }

}