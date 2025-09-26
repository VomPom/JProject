package com.vompom.media.codec.v2.docode

import android.media.MediaCodec
import android.view.Surface
import com.vompom.media.codec.v2.VLog
import java.nio.ByteBuffer

/**
 *
 * Created by @juliswang on 2025/09/24 21:36
 *
 * @Description
 */

class VideoDecoder(path: String, val surface: Surface) : BaseDecoder(path) {

    override fun render(buffer: ByteBuffer?, bufferInfo: MediaCodec.BufferInfo) {
        VLog.d("bufferInfo pts:${bufferInfo.presentationTimeUs / 1000 / 1000f}s size:${bufferInfo.size} offset: ${bufferInfo.offset}")
    }

    override fun configure(codec: MediaCodec) {
        codec.configure(extractor.getMediaFormat(), surface, null, 0)
    }

    override fun onInit() {

    }

    override fun decodeType(): IDecoder.DecodeType = IDecoder.DecodeType.Video

}