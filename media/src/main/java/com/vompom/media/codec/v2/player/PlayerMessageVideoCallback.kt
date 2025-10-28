package com.vompom.media.codec.v2.player

import android.os.Handler
import android.os.Message
import com.vompom.media.codec.v2.docode.model.PlayerMessage
import com.vompom.media.codec.v2.docode.track.IDecoderTrack
import com.vompom.media.codec.v2.utils.MessageUtils

/**
 *
 * Created by @juliswang on 2025/09/28 21:20
 *
 * @Description 专门处理 Video 消息
 */

class PlayerMessageVideoCallback(
    val player: VMPlayer,
    val playerThread: PlayerThread,
    val syncManager: AVSyncManager,
    val videoDecoderTrack: IDecoderTrack,
) : Handler.Callback {
    private var loop = false
    private var pause = false

    // todo:: 按配置的帧率来，不写死
    private var frameDurationUs = 1_000_000L / 30               // 一帧的时间
    private var nexDecodePosition: Long = 0                     // 下一帧解码将会进行的时间戳
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
        nexDecodePosition = findLastSeekTime() ?: targetUs
        mAudioThread?.sendMessage(PlayerThread.ACTION_PAUSE)
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
        if (msgId == PlayerThread.ACTION_SEEK) {
            videoDecoderTrack.seek(nexDecodePosition)
        }
        videoDecoderTrack.readSample(nexDecodePosition)
        syncManager.updateVideoTime(videoDecoderTrack.playedUs())
        nexDecodePosition = mAudioThread?.playedUs() ?: 0L
        syncManager.updateAudioTime(nexDecodePosition)
        scheduleReadSample()
    }

    private fun scheduleReadSample() {
        if (loop) {
            val audioPlayUs = mAudioThread?.playedUs() ?: 0L
            val videoPlayUs = videoDecoderTrack.playedUs()
            player.mMainHandler.obtainMessage(VMPlayer.TYPE_PROGRESS, audioPlayUs).sendToTarget()

            val diffTimeUs = syncManager.calculateWaitTime(audioPlayUs, videoPlayUs, pause)
            playerThread.sendMessageDelay(PlayerThread.ACTION_READ_SAMPLE, wait = diffTimeUs)
            nexDecodePosition += frameDurationUs
        }
    }
}