package com.vompom.media.codec.v2.docode

import com.vompom.media.codec.v2.BaseTest
import org.junit.Test

/**
 *
 * Created by @juliswang on 2025/09/25 20:15
 *
 * @Description
 */

class VideoDecoderTest : BaseTest() {
    @Test
    fun videoDecoder() {
        val decoder = VideoDecoder(mp4Path)
        Thread(decoder).start()
        decoder.pause()
    }
}