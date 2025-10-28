package com.vompom.media.codec.v2.docode.track

import android.view.Surface
import com.vompom.media.codec.v2.docode.decorder.IDecoder
import com.vompom.media.codec.v2.docode.decorder.VideoDecoder
import com.vompom.media.codec.v2.docode.model.SampleState
import com.vompom.media.codec.v2.docode.model.TrackSegment
import com.vompom.media.codec.v2.utils.VLog

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

    override fun readSample(playTimeUs: Long): SampleState {
        VLog.d("video readSample playTimeUs:$playTimeUs")
        if (isNeedDecodeNext(playTimeUs)) {
            nextSegment()
        }
        val readSampleTimeUs = calSegmentSampleTime(playTimeUs)
        // todo:: 首帧播放的时候，可能需要一次 seek 操作
        val state = currentDecoder!!.readSample(readSampleTimeUs)
        updateCurrentPlayUs(state.frameTimeUs)
        // 准备下一个片段的 Codec
        if (state.stateCode == IDecoder.SAMPLE_STATE_FINISH) {
            nextSegment()
        }
        return state
    }
}