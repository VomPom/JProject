package com.vompom.media.codec.v2.player

import android.os.Handler
import android.os.Message
import com.vompom.media.codec.v2.docode.track.IDecoderTrack
import com.vompom.media.codec.v2.utils.usToMs
import wang.julis.jwbase.utils.Logger

/**
 *
 * Created by @juliswang on 2025/09/28 21:20
 *
 * @Description
 */

class PlayerMessageHandler(
    val player: VMPlayer,
    val playerThread: PlayerThreadVideo,
    val videoDecoderTrack: IDecoderTrack,
) : Handler.Callback {
    private var loop = false
    private var pause = false
    private var mAudioThread: PlayerThreadAudio? = null
    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            PlayerThreadVideo.ACTION_PREPARE -> prepare()

            PlayerThreadVideo.ACTION_PLAY -> play()

            PlayerThreadVideo.ACTION_PAUSE -> pause()

            PlayerThreadVideo.ACTION_SEEK -> seek(msg.obj as Long)

            PlayerThreadVideo.ACTION_STOP -> stop()

            PlayerThreadVideo.ACTION_RELEASE -> release()

            PlayerThreadVideo.ACTION_READ_SAMPLE -> readSample(msg.obj as Long)

        }
        syncAudioOtherMsg(msg.what, msg.obj)
        return true
    }

    private fun syncAudioOtherMsg(action: Int, obj: Any?) {
        if (action == PlayerThreadVideo.ACTION_PAUSE
            || action == PlayerThreadVideo.ACTION_PLAY
            || action == PlayerThreadVideo.ACTION_PREPARE
            || action == PlayerThreadVideo.ACTION_STOP
            || action == PlayerThreadVideo.ACTION_SEEK
            || action == PlayerThreadVideo.ACTION_RELEASE
        ) {
            mAudioThread?.sendMessage(action, obj)
        }
    }

    fun setAudioThread(mAudioThread: PlayerThreadAudio) {
        this.mAudioThread = mAudioThread
    }

    private fun prepare() {
        videoDecoderTrack.prepare()
    }

    private fun play() {
        loop = true
        pause = false
        readSample(0)
    }

    private fun pause() {
        loop = false
        pause = true
    }

    private fun stop() {
    }

    private fun release() {
        videoDecoderTrack.release()
    }

    private fun seek(positionUs: Long) {

    }

    private fun readSample(targetTime: Long) {
        videoDecoderTrack.readSample(targetTime)
        scheduleReadSample()
    }

    private fun scheduleReadSample() {
        if (loop) {
            playerThread.sendMessageDelay(PlayerThreadVideo.ACTION_READ_SAMPLE, 0L, calWaitTime())
        }
    }

    /**
     * 针对音频和视频画面进行同步，以音频播放的时间为准，视频画面进行等待
     *
     * @return 视频所需要等待的时间
     */
    private fun calWaitTime(): Long {
        val audioPlayUs = mAudioThread?.getCurrentPlayUs() ?: 0L
        val videoPlayUs = videoDecoderTrack.getCurrentPlayUs()
        val waitTime = usToMs(videoPlayUs - audioPlayUs)
        Logger.d("--julis videoPlayUs=$videoPlayUs,audioPlayUs=$audioPlayUs,waitTime=$waitTime")
        return waitTime
    }

}