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
 * @Description 音视频同步管理器，负责协调音频和视频的播放时序，确保音画同步
 *
 * 主要功能：
 * 1. 实现音视频同步算法，以音频时钟为基准，调节视频播放速度
 * 2. 计算视频帧的等待时间，控制视频帧的渲染时机以匹配音频进度
 * 3. 处理seek操作期间的同步状态，避免seek时的音视频不同步问题
 * 4. 检测片段边界跳跃，在多片段播放时提供快速同步策略
 * 5. 监控同步质量，统计同步误差和连续错误次数
 * 6. 提供同步阈值管理，区分正常同步、快速追赶和限制等待等不同策略
 * 7. 支持暂停状态处理，暂停时不进行同步计算
 *
 * 同步策略：
 * - 正常情况：视频等待音频，确保音画同步
 * - 音频超前过多：视频快速追赶，避免长时间延迟
 * - 视频超前过多：限制等待时间，防止卡顿
 * - seek期间：跳过同步检查，快速完成定位
 *
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
            VLog.d("Video ahead too much, limited wait, audioTime:${audioTimeUs} videoTime:${videoTimeUs} waitTime=${waitTime}ms")
            return waitTime
        }

        // 正常同步等待
        val waitTime = if (diffUs > 0) usToMs(diffUs) else 0L

        if (absDiff > SYNC_THRESHOLD_US) {
            VLog.d(
                "videoPlayS=${usToS(videoTimeUs.toFloat())}," +
                        "audioPlayS=${usToS(audioTimeUs.toFloat())}," +
                        "waitTimeS=${msToS(waitTime.toFloat())}"
            )
        }
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