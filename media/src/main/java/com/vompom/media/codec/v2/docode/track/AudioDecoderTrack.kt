package com.vompom.media.codec.v2.docode.track

import com.vompom.media.codec.v2.docode.TrackSegment
import com.vompom.media.codec.v2.docode.decorder.IDecoder

/**
 *
 * Created by @juliswang on 2025/10/10 18:43
 *
 * @Description
 */

class AudioDecoderTrack : IDecoderTrack {
    constructor(segmentList: List<TrackSegment>) {
        setTrackSegments(segmentList)
        setDecodeType(IDecoder.DecodeType.Video)
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun setTrackSegments(segmentList: List<TrackSegment>) {
        TODO("Not yet implemented")
    }

    override fun setDecodeType(decoderType: IDecoder.DecodeType) {
        TODO("Not yet implemented")
    }

    override fun readSample(targetTime: Long) {
        TODO("Not yet implemented")
    }

    override fun release() {
        TODO("Not yet implemented")
    }
}