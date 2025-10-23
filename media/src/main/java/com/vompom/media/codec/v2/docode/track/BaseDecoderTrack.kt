package com.vompom.media.codec.v2.docode.track

import com.vompom.media.codec.v2.docode.decorder.IDecoder
import com.vompom.media.codec.v2.docode.model.TrackSegment

/**
 *
 * Created by @juliswang on 2025/10/20 20:14
 *
 * @Description
 */

abstract class BaseDecoderTrack : IDecoderTrack {
    protected var currentSegmentIndex = -1
    protected var segmentList = mutableListOf<TrackSegment>()
    protected var decodeType: IDecoder.DecodeType = IDecoder.DecodeType.Video
    protected var currentDecoder: IDecoder? = null
    protected val decoderLock = Any()

    override fun setTrackSegments(segmentList: List<TrackSegment>) {
        this.segmentList.apply {
            clear()
            addAll(segmentList)
        }
    }

    protected fun currentSegment(): TrackSegment {
        return segmentList[currentSegmentIndex]
    }

    protected fun releaseCurrentDecoder() {
        if (currentDecoder == null) {
            return
        }
        synchronized(decoderLock) {
            currentDecoder?.apply {
                release()
            }
        }
    }

    protected fun nextSegment() {
        if (currentSegmentIndex + 1 < segmentList.size) {
            currentSegmentIndex++
        } else {
            currentSegmentIndex = 0
        }
        doCreateDecoder()
    }

    fun doCreateDecoder() {
        releaseCurrentDecoder()
        val segment = currentSegment()
        synchronized(decoderLock) {
            currentDecoder = createDecoder(segment)
        }
    }

    /**
     * 进行 Seek 操作
     * 如果当前的 segment 就是目标 segment，则直接调用 decoder 的 seek 方法，否则需要先切换 segment 再进行 seek
     *
     * @param targetUs 相对整个播放器时长的目标位置
     */
    override fun seek(targetUs: Long) {
        val result = findSegmentInfo(targetUs)
        if (result == null) return
        val (segment, segmentIndex) = result
        // 获取在目标 seek 时间 在 segment 中的位置
        val segmentSeekTimeUs = targetUs - segment.startUs

        if (segmentIndex == currentSegmentIndex) {
            currentDecoder?.seek(segmentSeekTimeUs)
        } else {
            currentSegmentIndex = segmentIndex
            doCreateDecoder()
            currentDecoder?.seek(segmentSeekTimeUs)
        }
    }

    private fun findSegmentInfo(targetUs: Long): Pair<TrackSegment, Int>? {
        segmentList.forEachIndexed { index, segment ->
            if (segment.startUs <= targetUs && targetUs < (segment.startUs + segment.durationUs)) {
                return Pair(segment, index)
            }
        }
        return null
    }

    override fun release() {
        currentDecoder?.release()
    }

    abstract fun createDecoder(segment: TrackSegment): IDecoder
}