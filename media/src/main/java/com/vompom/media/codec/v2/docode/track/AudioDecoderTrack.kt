package com.vompom.media.codec.v2.docode.track

import com.vompom.media.codec.v2.docode.decorder.AudioDecoder
import com.vompom.media.codec.v2.docode.decorder.IDecoder
import com.vompom.media.codec.v2.docode.model.SampleState
import com.vompom.media.codec.v2.docode.model.TrackSegment

/**
 *
 * Created by @juliswang on 2025/10/10 18:43
 *
 * @Description
 */

class AudioDecoderTrack() : BaseDecoderTrack() {
    constructor(segmentList: List<TrackSegment>) : this() {
        setTrackSegments(segmentList)
        decodeType = IDecoder.DecodeType.Audio
    }

    override fun prepare() {
        nextSegment()
    }

    override fun createDecoder(segment: TrackSegment): IDecoder {
        val decoder = AudioDecoder(segment.asset)
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


    /**
     * 判断当前需要读取帧的时间大于当前资源的时间
     */
    private fun exceedTime(targetTimeUs: Long): Boolean {
        val segment = currentSegment()
        val audioDurationUs = segment.startUs + segment.durationUs
        return audioDurationUs <= targetTimeUs
    }

    override fun getCurrentPlayUs(): Long = (currentDecoder?.getCurrentPlayUs() ?: 0L) + currentSegment().startUs
}