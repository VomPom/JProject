package com.vompom.media.codec.v2.docode.decorder

import android.media.MediaCodec
import android.view.Surface
import com.vompom.media.codec.v2.utils.VLog
import com.vompom.media.codec.v2.utils.usToS
import java.nio.ByteBuffer

/**
 *
 * Created by @juliswang on 2025/09/24 21:36
 *
 * @Description
 */

class VideoDecoder(val path: String, val surface: Surface) : BaseDecoder(path) {
    private var currentPlayPositionUs = 0L
    private var cnt = 0
    override fun render(buffer: ByteBuffer?, bufferInfo: MediaCodec.BufferInfo) {
        cnt++
        currentPlayPositionUs = bufferInfo.presentationTimeUs
        VLog.d("video pts:${usToS(bufferInfo.presentationTimeUs)}s size:${bufferInfo.size} offset: ${bufferInfo.offset} cnt: $cnt")
    }

    override fun readSample(targetTimeUs: Long) {
        // 向 MediaCodec 添加解码的数据，在没有 EOS 之前一直添加
        if (!isEOS) {
            val bufferTime = fillBufferToDecoder()
            VLog.d("--julis audio readSample targetTimeUs: $targetTimeUs got bufferTime:$bufferTime")
        }

        // 从 MediaCodec 队列中获取解码后的数据
        if (!isDecodeDone) {
            fetchBufferFromDecoder()
        }
    }

    override fun configure(codec: MediaCodec) {
        codec.configure(extractor.getMediaFormat(), surface, null, 0)
    }

    override fun onPrepare() {
        // no-op
    }

    override fun decodeType(): IDecoder.DecodeType = IDecoder.DecodeType.Video

    override fun getCurrentPlayUs(): Long = currentPlayPositionUs
}