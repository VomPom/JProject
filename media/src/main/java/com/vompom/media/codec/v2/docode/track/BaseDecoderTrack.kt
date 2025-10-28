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
    protected var currentPlayUs: Long = 0L

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
    override fun seek(targetUs: Long): Long {
        val result = findSegmentInfo(targetUs)
        if (result == null) return targetUs
        val (segment, segmentIndex) = result
        // 获取在目标 seek 时间 在 segment 中的位置
        val segmentSeekTimeUs = targetUs - segment.timelineRange.startUs + segment.asset.sourceRange.startUs
        var ptsAfterSeek: Long? = targetUs
        if (segmentIndex == currentSegmentIndex) {
            ptsAfterSeek = currentDecoder?.seek(segmentSeekTimeUs)
        } else {
            currentSegmentIndex = segmentIndex
            doCreateDecoder()
            ptsAfterSeek = currentDecoder?.seek(segmentSeekTimeUs)
        }
        return ptsAfterSeek ?: targetUs
    }

    /**
     * 通过当前播放时间计算当前片段的采样时间
     * @param targetTime  在时间轨道上面的播放时间
     * @return  在对应的片段资源上的时间
     */
    fun calSegmentSampleTime(targetTime: Long): Long {
        val segment = currentSegment()
        val segmentStartTime = segment.timelineRange.startUs
        val segmentEndTime = segment.timelineRange.endUs

        // 需要重新播放当前视频
        if (targetTime >= segmentEndTime) {
            return segment.sourceRange.startUs
        }
        // 理论上来说应该都是走到这个逻辑里面
        // 真正要读取时间为：播放时间-片段在时间轴上开始的时间+片段资源开始的时间
        if (targetTime >= segmentStartTime) {
            return targetTime - segmentStartTime + segment.sourceRange.startUs
        }
//        throw RuntimeException("calSegmentSampleTime error,targetTime:${targetTime} segmentStartTime:${segmentStartTime}")
        return targetTime
    }

    /**
     * 判断当前需要读取帧的时间大于当前资源的时间，或者是否是超出当前资源片段的时间
     *
     * @param targetTimeUs 在时间轨道上面的播放时间
     * @return   true 依然需要播放当前的资源，false 需要跳转到下一个 decoder 解码下一个数据
     */
    fun isNeedDecodeNext(targetTimeUs: Long): Boolean {
        val segment = currentSegment()
        return segment.timelineRange.endUs <= targetTimeUs
    }

    private fun findSegmentInfo(targetUs: Long): Pair<TrackSegment, Int>? {
        segmentList.forEachIndexed { index, segment ->
            if (segment.timelineRange.startUs <= targetUs && targetUs < (segment.timelineRange.endUs)) {
                return Pair(segment, index)
            }
        }
        return null
    }

    /**
     * 更新当前播放的时间，这个时间是在整个时间轴上的时间，计算公式为：
     *      当前资源播放过的时间 +当前资源在时间轴上开始的时间
     * @param segmentPTS 在某个片段上的 pts 时间
     */
    protected fun updateCurrentPlayUs(segmentPTS: Long) {
        val segment = currentSegment()
        var sourcePlayedUs = segmentPTS - segment.sourceRange.startUs
        if (sourcePlayedUs < 0) {
            sourcePlayedUs = 0 // 可能在刚解码的时候失败相关，则返回0
        }
        currentPlayUs = sourcePlayedUs + segment.timelineRange.startUs
    }

    abstract fun createDecoder(segment: TrackSegment): IDecoder

    override fun playedUs(): Long = currentPlayUs

    override fun release() {
        currentDecoder?.release()
    }
}