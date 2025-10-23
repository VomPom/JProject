package com.vompom.media.codec.v2.docode.decorder

import android.media.MediaCodec
import android.view.Surface
import com.vompom.media.codec.v2.docode.model.Asset
import com.vompom.media.codec.v2.docode.model.SampleState
import com.vompom.media.codec.v2.utils.VLog
import com.vompom.media.codec.v2.utils.usToS
import java.nio.ByteBuffer

/**
 *
 * Created by @juliswang on 2025/09/24 21:36
 *
 * @Description
 */

class VideoDecoder(val asset: Asset, val surface: Surface) : BaseDecoder(asset) {
    private var currentPlayPositionUs = 0L
    private var cnt = 0
    override fun render(buffer: ByteBuffer?, bufferInfo: MediaCodec.BufferInfo) {
        cnt++
        currentPlayPositionUs = bufferInfo.presentationTimeUs
        VLog.v("video pts:${usToS(bufferInfo.presentationTimeUs)}s size:${bufferInfo.size} offset: ${bufferInfo.offset} cnt: $cnt")
    }

    override fun readSample(targetTimeUs: Long): SampleState {
        if (isReleased) {
            return SampleState()
        }
        // 向 MediaCodec 添加解码的数据，在没有 EOS 之前一直添加
        var needRender = true
        var sampleState = SampleState()
        if (!readSampleDone) {
            val bufferTime = doReadSample()
            needRender = bufferTime >= targetTimeUs
        }

        // 从 MediaCodec 队列中获取解码后的数据
        if (!isDecodeDone) {
            sampleState = renderBuffer(needRender)
        }
        // seek 逻辑可能会触发这里，查找帧如果没有找到则一直循环查找
        if (!needRender && !isDecodeDone) {
            sampleState = readSample(targetTimeUs)
        }
        return sampleState
    }

    override fun configure(codec: MediaCodec) {
        try {
            codec.configure(extractor.getMediaFormat(), surface, null, 0)
        } catch (e: Exception) {
            VLog.e("VideoDecoder configure error: ${e.message}")
        }
    }

    override fun onPrepare() {
        // no-op
    }

    override fun decodeType(): IDecoder.DecodeType = IDecoder.DecodeType.Video

    override fun getCurrentPlayUs(): Long = currentPlayPositionUs
}