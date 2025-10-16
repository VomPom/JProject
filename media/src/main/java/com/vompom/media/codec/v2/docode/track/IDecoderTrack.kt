package com.vompom.media.codec.v2.docode.track

import com.vompom.media.codec.v2.docode.TrackSegment
import com.vompom.media.codec.v2.docode.decorder.IDecoder

/**
 *
 * Created by @juliswang on 2025/10/10 18:42
 *
 * @Description
 */

interface IDecoderTrack {
    fun start()
    fun setTrackSegments(segmentList: List<TrackSegment>)
    fun setDecodeType(decoderType: IDecoder.DecodeType)
    fun readSample(targetTime: Long)
    fun release()
}