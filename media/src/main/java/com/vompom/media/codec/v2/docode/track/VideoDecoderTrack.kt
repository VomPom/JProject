package com.vompom.media.codec.v2.docode.track

import android.view.Surface
import com.vompom.media.codec.v2.docode.decorder.IDecoder
import com.vompom.media.codec.v2.docode.decorder.VideoDecoder
import com.vompom.media.codec.v2.docode.model.TrackSegment

/**
 *
 * Created by @juliswang on 2025/10/10 18:42
 *
 * @Description 负责视频轨道的管理
 */

class VideoDecoderTrack() : BaseDecoderTrack() {
    private lateinit var outputSurface: Surface

    constructor(segmentList: List<TrackSegment>, outputSurface: Surface) : this() {
        this.outputSurface = outputSurface
        setTrackSegments(segmentList)
        decodeType = IDecoder.DecodeType.Video
    }

    override fun prepare() {
        nextSegment()
    }

    override fun createDecoder(segment: TrackSegment): IDecoder {
        val decoder = VideoDecoder(segment.asset, outputSurface)
        decoder.prepare()
        return decoder
    }

}