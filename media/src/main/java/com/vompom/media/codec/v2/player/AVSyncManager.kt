package com.vompom.media.codec.v2.player

import com.vompom.media.codec.v2.utils.VLog
import com.vompom.media.codec.v2.utils.msToS
import com.vompom.media.codec.v2.utils.usToMs
import com.vompom.media.codec.v2.utils.usToS
import kotlin.math.abs

/**
 *
 * Created by @juliswang on 2025/10/28 21:15
 *
 * @Description
 */

class AVSyncManager {
    companion object {
        private const val SYNC_THRESHOLD_US = 100_000L      // 100ms同步阈值
        private const val MAX_ALLOWED_DIFF_US = 500_000L    // 500ms最大允许差异
        private const val SEGMENT_BOUNDARY_THRESHOLD_US = 200_000L  // 200ms段落边界检测阈值
    }

    private var lastValidAudioTime = 0L
    private var lastValidVideoTime = 0L
    private var isSeekInProgress = false
    private var segmentBoundaryDetected = false
    private var consecutiveSyncErrors = 0
    private var maxSyncError = 0L

    /**
     * 更新音频时间戳
     */
    fun updateAudioTime(audioTimeUs: Long) {
        detectSegmentBoundary(audioTimeUs, lastValidAudioTime)
        lastValidAudioTime = audioTimeUs
    }

    /**
     * 更新视频时间戳
     */
    fun updateVideoTime(videoTimeUs: Long) {
        detectSegmentBoundary(videoTimeUs, lastValidVideoTime)
        lastValidVideoTime = videoTimeUs
    }

    /**
     * 检测segment边界
     */
    private fun detectSegmentBoundary(currentTime: Long, lastTime: Long) {
        val timeDiff = abs(currentTime - lastTime)
        if (timeDiff > SEGMENT_BOUNDARY_THRESHOLD_US) {
            segmentBoundaryDetected = true
            VLog.d("Segment boundary detected: timeDiff=${timeDiff}us")
        } else {
            segmentBoundaryDetected = false
        }
    }

    /**
     * 设置seek状态
     */
    fun setSeekInProgress(inProgress: Boolean) {
        isSeekInProgress = inProgress
        if (inProgress) {
            // seek开始时重置状态
            segmentBoundaryDetected = false
            consecutiveSyncErrors = 0
        }
    }

    /**
     * 计算同步等待时间
     *
     * @param audioTimeUs   当前音频帧时间戳
     * @param videoTimeUs   当前视频帧时间戳
     * @param paused
     * @return
     */
    fun calculateWaitTime(audioTimeUs: Long, videoTimeUs: Long, paused: Boolean): Long {
        if (paused) return 0L

        val diffUs = videoTimeUs - audioTimeUs
        val absDiff = abs(diffUs)

        // 记录同步错误
        if (absDiff > SYNC_THRESHOLD_US) {
            consecutiveSyncErrors++
            maxSyncError = maxOf(maxSyncError, absDiff)
        } else {
            consecutiveSyncErrors = 0
        }

        // 在seek或segment边界时快速同步
        if (isSeekInProgress || segmentBoundaryDetected) {
            VLog.d("Fast sync: seek=$isSeekInProgress, boundary=$segmentBoundaryDetected")
            return 0L
        }

        // 如果音频远超前，允许视频快速追赶
        if (diffUs < -MAX_ALLOWED_DIFF_US) {
            VLog.d("Audio ahead too much, video catchup: diffUs=$diffUs")
            return 0L
        }

        // 如果视频远远超前，限制等待时间
        if (diffUs > MAX_ALLOWED_DIFF_US) {
            val waitTime = usToMs(diffUs - MAX_ALLOWED_DIFF_US)
            VLog.d("Video ahead too much, limited wait: waitTime=${waitTime}ms")
            return waitTime
        }

        // 正常同步等待
        val waitTime = if (diffUs > 0) usToMs(diffUs) else 0L

        if (absDiff > SYNC_THRESHOLD_US) {
            VLog.d("Sync adjustment: audioUs=$audioTimeUs, videoUs=$videoTimeUs, diffUs=$diffUs, waitMs=$waitTime")
        }
        VLog.d(
            "videoPlayS=${usToS(videoTimeUs.toFloat())},audioPlayS=${usToS(audioTimeUs.toFloat())},waitTimeS=${
                msToS(
                    waitTime.toFloat()
                )
            } pause=$paused"
        )
        return waitTime
    }

    /**
     * 获取同步状态信息
     */
    fun getSyncInfo(): String {
        return "SyncErrors: $consecutiveSyncErrors, MaxError: ${maxSyncError / 1000}ms, " +
                "Seek: $isSeekInProgress, Boundary: $segmentBoundaryDetected"
    }

    /**
     * 重置同步状态
     */
    fun reset() {
        lastValidAudioTime = 0L
        lastValidVideoTime = 0L
        isSeekInProgress = false
        segmentBoundaryDetected = false
        consecutiveSyncErrors = 0
        maxSyncError = 0L
    }
}