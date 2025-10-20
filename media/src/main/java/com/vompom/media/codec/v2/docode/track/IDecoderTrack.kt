package com.vompom.media.codec.v2.docode.track

import com.vompom.media.codec.v2.docode.model.SampleState
import com.vompom.media.codec.v2.docode.model.TrackSegment

/**
 *
 * Created by @juliswang on 2025/10/10 18:42
 *
 * @Description
 */

interface IDecoderTrack {
    fun prepare()
    fun setTrackSegments(segmentList: List<TrackSegment>)
    fun readSample(targetTime: Long): SampleState
    fun seek(targetUs: Long)
    fun release()
    fun getCurrentPlayUs(): Long
}