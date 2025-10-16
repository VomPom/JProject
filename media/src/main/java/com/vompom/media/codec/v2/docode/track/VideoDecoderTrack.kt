package com.vompom.media.codec.v2.docode.track

import android.view.Surface
import com.vompom.media.codec.v2.docode.TrackSegment
import com.vompom.media.codec.v2.docode.decorder.IDecoder
import com.vompom.media.codec.v2.docode.decorder.VideoDecoder

/**
 *
 * Created by @juliswang on 2025/10/10 18:42
 *
 * @Description
 */

class VideoDecoderTrack : IDecoderTrack {
    private var segmentList = mutableListOf<TrackSegment>()
    private var decodeType: IDecoder.DecodeType = IDecoder.DecodeType.Video
    private var outputSurface: Surface
    private var currentSegmentIndex = 0
    private var lastSampleTime: Long = 0L
    private var currentDecoder: IDecoder? = null

    constructor(segmentList: List<TrackSegment>, outputSurface: Surface) {
        this.outputSurface = outputSurface
        setTrackSegments(segmentList)
        setDecodeType(IDecoder.DecodeType.Video)
    }


    override fun prepare() {
        createDecoder()
    }

    private fun createDecoder() {
        val segment = getCurrentSegment()
        currentDecoder = VideoDecoder(segment.path, outputSurface)
        currentDecoder?.prepare()
    }

    private fun getCurrentSegment(): TrackSegment {
        return segmentList[currentSegmentIndex]
    }

    override fun setTrackSegments(segmentList: List<TrackSegment>) {
        this.segmentList.apply {
            clear()
            addAll(segmentList)
        }
    }

    override fun setDecodeType(decoderType: IDecoder.DecodeType) {
        this.decodeType = decoderType
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

    override fun getCurrentPlayUs(): Long = currentDecoder?.getCurrentPlayUs() ?: 0L
}