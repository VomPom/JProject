package com.vompom.media.codec.v2.docode.track

import android.view.Surface
import com.vompom.media.codec.v2.docode.decorder.IDecoder
import com.vompom.media.codec.v2.docode.decorder.VideoDecoder
import com.vompom.media.codec.v2.docode.model.SampleState
import com.vompom.media.codec.v2.docode.model.TrackSegment

/**
 *
 * Created by @juliswang on 2025/10/10 18:42
 *
 * @Description
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

    override fun readSample(targetTime: Long): SampleState {
        val state = currentDecoder!!.readSample(targetTime)
        // 准备下一个片段的 Codec
        if (state.stateCode == IDecoder.SAMPLE_STATE_FINISH
            || exceedTime(targetTime)
        ) {
            nextSegment()
        }
        return state
    }

    override fun getCurrentPlayUs(): Long = (currentDecoder?.getCurrentPlayUs() ?: 0L) + getCurrentSegment().startUs()

    /**
     * 判断当前需要读取帧的时间大于当前资源的时间
     */
    private fun exceedTime(targetTimeUs: Long): Boolean {
        val segment = getCurrentSegment()
        return segment.startUs() + segment.durationUs() <= targetTimeUs
    }


    class DecoderWrapper() {
        var decoder: IDecoder? = null
        var segment: TrackSegment? = null
        var segmentIndex: Int = -1
    }
}