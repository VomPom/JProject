package com.vompom.media.codec.v2.docode.track

import com.vompom.media.codec.v2.docode.decorder.AudioDecoder
import com.vompom.media.codec.v2.docode.decorder.IDecoder
import com.vompom.media.codec.v2.docode.model.TrackSegment

/**
 *
 * Created by @juliswang on 2025/10/10 18:43
 *
 * @Description 负责音频轨道的数据管理
 */

class AudioDecoderTrack() : BaseDecoderTrack() {
    constructor(segmentList: List<TrackSegment>) : this() {
        setTrackSegments(segmentList)
        decodeType = IDecoder.DecodeType.Audio
    }

    override fun prepare() {
        nextSegment()
    }

    override fun seek(targetUs: Long): Long {
        currentPlayUs = targetUs
        return super.seek(targetUs)
    }

    override fun createDecoder(segment: TrackSegment): IDecoder {
        val decoder = AudioDecoder(segment.asset)
        decoder.prepare()
        return decoder
    }

}