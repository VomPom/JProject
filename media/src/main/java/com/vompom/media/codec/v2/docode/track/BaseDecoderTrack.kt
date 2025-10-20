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

    protected fun getCurrentSegment(): TrackSegment {
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
        val segment = getCurrentSegment()
        synchronized(decoderLock) {
            currentDecoder = createDecoder(segment)
        }
    }

    override fun seek(targetUs: Long) {
        val result = findSegmentIndex(targetUs)
        if (result == null) return
        val (segment, segmentIndex) = result
        if (segmentIndex == currentSegmentIndex) {
            currentDecoder?.seek(targetUs)
        } else {
            currentSegmentIndex = segmentIndex
            doCreateDecoder()
            currentDecoder?.seek(targetUs - segment.starUs)
        }
    }

    override fun release() {
        currentDecoder?.release()
    }

    private fun findSegmentIndex(targetUs: Long): Pair<TrackSegment, Int>? {
        segmentList.forEachIndexed { index, segment ->
            if (segment.starUs <= targetUs && targetUs < (segment.starUs + segment.durationUs())) {
                return Pair(segment, index)
            }
        }
        return null
    }

    abstract fun createDecoder(segment: TrackSegment): IDecoder
}