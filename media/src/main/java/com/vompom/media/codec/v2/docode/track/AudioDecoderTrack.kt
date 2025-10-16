package com.vompom.media.codec.v2.docode.track

import com.vompom.media.codec.v2.docode.TrackSegment
import com.vompom.media.codec.v2.docode.decorder.AudioDecoder
import com.vompom.media.codec.v2.docode.decorder.IDecoder

/**
 *
 * Created by @juliswang on 2025/10/10 18:43
 *
 * @Description
 */

class AudioDecoderTrack : IDecoderTrack {
    private var currentDecoder: IDecoder? = null
    private var segmentList = mutableListOf<TrackSegment>()
    private var currentSegmentIndex = 0

    constructor(segmentList: List<TrackSegment>) {
        setTrackSegments(segmentList)
        setDecodeType(IDecoder.DecodeType.Audio)
    }

    override fun prepare() {
        createDecoder()
    }

    private fun createDecoder() {
        val segment = getCurrentSegment()
        currentDecoder = AudioDecoder(segment.path)
        currentDecoder?.prepare()
    }

    override fun setTrackSegments(segmentList: List<TrackSegment>) {
        this.segmentList.apply {
            clear()
            addAll(segmentList)
        }
    }

    override fun setDecodeType(decoderType: IDecoder.DecodeType) {
    }

    override fun readSample(targetTime: Long) {
        currentDecoder?.readSample(targetTime)
    }

    override fun seek(targetUs: Long) {
        currentDecoder?.seek(targetUs)
    }

    override fun release() {
        currentDecoder?.release()
    }

    private fun getCurrentSegment(): TrackSegment {
        return segmentList[currentSegmentIndex]
    }

    override fun getCurrentPlayUs(): Long = currentDecoder?.getCurrentPlayUs() ?: 0L
}