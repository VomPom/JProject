package com.vompom.media.codec.v2.player

import android.os.Handler
import android.os.Message
import com.vompom.media.codec.v2.docode.model.PlayerMessage
import com.vompom.media.codec.v2.docode.model.SampleState
import com.vompom.media.codec.v2.docode.track.IDecoderTrack
import com.vompom.media.codec.v2.utils.MessageUtils
import com.vompom.media.codec.v2.utils.VLog
import com.vompom.media.codec.v2.utils.msToS
import com.vompom.media.codec.v2.utils.usToMs
import com.vompom.media.codec.v2.utils.usToS

/**
 *
 * Created by @juliswang on 2025/09/28 21:20
 *
 * @Description 专门处理 Video 消息
 */

class PlayerMessageVideoCallback(
    val player: VMPlayer,
    val playerThread: PlayerThread,
    val videoDecoderTrack: IDecoderTrack,
) : Handler.Callback {
    private var loop = false
    private var pause = false
    private var frameDurationUs = 1_000_000L / 30                   // 一帧的时间
    private var lastSampleState = SampleState()
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

            PlayerThread.ACTION_READ_SAMPLE -> readSample(msg.what)

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
        readSample(PlayerThread.ACTION_READ_SAMPLE)
    }

    private fun pause() {
        loop = false
        pause = true
    }

    private fun stop() {
    }

    private fun release() {
        playerThread.handlerThread?.quitSafely()
        mAudioThread?.handlerThread?.quitSafely()
        videoDecoderTrack.release()
    }

    /**
     * 进行 seek 操作，整体的策略是：如果在短时间内有多个 seek message，则取最后一个，并放弃掉其他的 seek message。
     * 在 seek 之前先比较当前的时间戳与关键帧的位置，如果当前的时间位于两个关键帧之间，那么不需要 seek 到关键帧位置，
     * 直接从当前位置进行解码直到目标的时间戳，否则从关键帧开始解码，这在 GOP 比较间隔比较远的时候非常有用。
     */
    private fun seek(targetUs: Long) {
        val lastSeekTimeUs = findLastSeekTime() ?: targetUs
        mAudioThread?.sendMessage(PlayerThread.ACTION_PAUSE)
        lastSampleState = SampleState(lastSeekTimeUs)
        readSample(PlayerThread.ACTION_SEEK)
    }

    private fun findLastSeekTime(): Long? {
        val message =
            MessageUtils.getLastMessageObjAndRemoveOther(PlayerThread.ACTION_SEEK, playerThread.playHandler)
                ?: return null
        val targetTimeUs = ((message.obj as PlayerMessage?)?.obj1 as Long)
        playerThread.playHandler?.removeMessages(message.what, message.obj)
        return targetTimeUs
    }

    private fun readSample(msgId: Int) {
        val targetTime = lastSampleState.frameTimeUs
        if (msgId == PlayerThread.ACTION_SEEK) {
            // removePendingMessage(PlayerThread.ACTION_READ_SAMPLE)
            videoDecoderTrack.seek(targetTime)
        }
        lastSampleState = videoDecoderTrack.readSample(targetTime)
        scheduleReadSample()
    }

    private fun scheduleReadSample() {
        if (loop) {
            val audioPlayUs = mAudioThread?.getCurrentPlayUs() ?: 0L
            player.mMainHandler.obtainMessage(VMPlayer.TYPE_PROGRESS, audioPlayUs).sendToTarget()
            val diffTimeUs = calDiffTime(audioPlayUs)
            playerThread.sendMessageDelay(PlayerThread.ACTION_READ_SAMPLE, wait = diffTimeUs)
            lastSampleState.frameTimeUs += frameDurationUs
        }
    }

    /**
     * 针对音频和视频画面进行同步，以音频播放的时间为准，视频画面进行等待
     *
     * @return 视频所需要等待的时间
     */
    private fun calDiffTime(audioPlayUs: Long): Long {
        // todo:: 没有音频轨道的情况
        // todo:: 优化声音比视频画面快的情况
        val videoPlayUs = videoDecoderTrack.getCurrentPlayUs()
        var waitTime = if (pause) 0L else usToMs(videoPlayUs - audioPlayUs)
        VLog.v(
            "videoPlayS=${usToS(videoPlayUs.toFloat())},audioPlayS=${usToS(audioPlayUs.toFloat())},waitTimeS=${
                msToS(
                    waitTime.toFloat()
                )
            } pause=$pause"
        )
        if (waitTime > 1 || waitTime < -1) {
            VLog.d(
                "videoPlayS=${usToS(videoPlayUs.toFloat())},audioPlayS=${usToS(audioPlayUs.toFloat())},waitTimeS=${
                    msToS(
                        waitTime.toFloat()
                    )
                } pause=$pause"
            )
        }
        return waitTime
    }


}