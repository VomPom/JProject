package com.vompom.media.codec.v2.player

import android.os.Handler
import android.os.Message
import com.vompom.media.codec.v2.docode.model.PlayerMessage
import com.vompom.media.codec.v2.docode.track.IDecoderTrack
import com.vompom.media.codec.v2.utils.MessageUtils
import com.vompom.media.codec.v2.utils.VLog
import com.vompom.media.codec.v2.utils.msToS
import com.vompom.media.codec.v2.utils.usToMs
import com.vompom.media.codec.v2.utils.usToS
import wang.julis.jwbase.utils.Logger

/**
 *
 * Created by @juliswang on 2025/09/28 21:20
 *
 * @Description 专门处理 Video
 */

class PlayerMessageVideoHandler(
    val player: VMPlayer,
    val playerThread: PlayerThread,
    val videoDecoderTrack: IDecoderTrack,
) : Handler.Callback {
    private var loop = false
    private var pause = false
    private var mAudioThread: PlayerThreadAudio? = null

    override fun handleMessage(msg: Message): Boolean {
        val playerMessage: PlayerMessage? = if (msg.obj == null) null else msg.obj as PlayerMessage
        syncAudioMsg(msg.what, playerMessage?.obj1)

        when (msg.what) {
            PlayerThread.ACTION_PREPARE -> prepare()

            PlayerThread.ACTION_PLAY -> play()

            PlayerThread.ACTION_PAUSE -> pause()

            PlayerThread.ACTION_SEEK -> seek(playerMessage?.obj1 as Long)

            PlayerThread.ACTION_STOP -> stop()

            PlayerThread.ACTION_RELEASE -> release()

            PlayerThread.ACTION_READ_SAMPLE -> readSample(msg.what, playerMessage?.obj1 as Long)

        }
        return true
    }

    private fun syncAudioMsg(action: Int, obj: Any?) {
        if (action == PlayerThread.ACTION_PAUSE
            || action == PlayerThread.ACTION_PLAY
            || action == PlayerThread.ACTION_PREPARE
            || action == PlayerThread.ACTION_STOP
            || action == PlayerThread.ACTION_SEEK
            || action == PlayerThread.ACTION_RELEASE
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
        readSample(PlayerThread.ACTION_READ_SAMPLE, videoDecoderTrack.getCurrentPlayUs())
        VLog.d("--julis play...")
    }

    private fun pause() {
        loop = false
        pause = true
        VLog.d("--julis pause...")
    }

    private fun stop() {
    }

    private fun release() {
        videoDecoderTrack.release()
    }

    private fun seek(targetUs: Long) {
        val seekMessageList = MessageUtils.getMessageByWhat(PlayerThread.ACTION_READ_SAMPLE, player.mMainHandler)
        Logger.d("--julis seek targetS=${usToS(targetUs.toFloat())} seekMessageList size:${seekMessageList.size}")
        readSample(PlayerThread.ACTION_SEEK, targetUs)

    }

    private fun getLastSeekPosAndRemoveOther() {

    }

    private fun readSample(msgId: Int, targetTime: Long) {
        if (msgId == PlayerThread.ACTION_SEEK) {
            // removePendingMessage(PlayerThread.ACTION_READ_SAMPLE)
            videoDecoderTrack.seek(targetTime)
            videoDecoderTrack.readSample(targetTime)
            scheduleReadSample(true)
        } else {
            videoDecoderTrack.readSample(targetTime)
            scheduleReadSample(false)
        }

    }

    private fun scheduleReadSample(isFromSeek: Boolean) {
        if (loop) {
            val audioPlayUs = mAudioThread?.getCurrentPlayUs() ?: 0L
            player.mMainHandler.obtainMessage(VMPlayer.TYPE_PROGRESS, audioPlayUs).sendToTarget()
            val diffTimeUs = calDiffTime(audioPlayUs, isFromSeek)
            playerThread.sendMessageDelay(PlayerThread.ACTION_READ_SAMPLE, 0L, diffTimeUs)
        } else {
            VLog.d("--julis loop is false, stop scheduleReadSample")
        }
    }

    /**
     * 针对音频和视频画面进行同步，以音频播放的时间为准，视频画面进行等待
     *
     * @return 视频所需要等待的时间
     */
    private fun calDiffTime(audioPlayUs: Long, isFromSeek: Boolean): Long {
        // todo:: 没有音频轨道的情况
        val videoPlayUs = videoDecoderTrack.getCurrentPlayUs()
        var waitTime = if (isFromSeek || pause) 0L else usToMs(videoPlayUs - audioPlayUs)
        Logger.d(
            "--julis videoPlayS=${usToS(videoPlayUs.toFloat())},audioPlayS=${usToS(audioPlayUs.toFloat())},waitTimeS=${
                msToS(
                    waitTime.toFloat()
                )
            } pause=$pause"
        )
        return waitTime
    }


}