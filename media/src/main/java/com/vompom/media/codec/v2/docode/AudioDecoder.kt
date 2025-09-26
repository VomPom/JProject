package com.vompom.media.codec.v2.docode

import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaCodec
import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 *
 * Created by @juliswang on 2025/09/24 21:43
 *
 * @Description
 */

class AudioDecoder(path: String) : BaseDecoder(path) {
    private lateinit var audioTrack: AudioTrack

    override fun render(buffer: ByteBuffer?, bufferInfo: MediaCodec.BufferInfo) {
        if (buffer != null) {
            audioTrack.write(buffer, bufferInfo.size, AudioTrack.WRITE_BLOCKING)
        }
    }

    override fun configure(codec: MediaCodec) {
        codec.configure(extractor.getMediaFormat(), null, null, 0)
    }

    override fun onInit() {
        initRender()
    }

    private fun initRender() {
        val format = extractor.getMediaFormat()
        val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
        val channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
        val encoding = if (format.containsKey(MediaFormat.KEY_PCM_ENCODING)) {
            format.getInteger(MediaFormat.KEY_PCM_ENCODING)
        } else {
            AudioFormat.ENCODING_PCM_16BIT
        }

        val channel = if (channelCount == 1) {
            AudioFormat.CHANNEL_OUT_MONO    // 单声道
        } else {
            AudioFormat.CHANNEL_OUT_STEREO  // z双声道
        }

        val minBufferSize = AudioTrack.getMinBufferSize(sampleRate, channel, encoding)

        audioTrack = AudioTrack.Builder()
            .setAudioFormat(
                AudioFormat
                    .Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(encoding)
                    .build()
            )
            .setBufferSizeInBytes(minBufferSize)
            .build()
        audioTrack.play()
    }

    override fun release() {
        super.release()
        audioTrack.release()
    }

    override fun decodeType(): IDecoder.DecodeType = IDecoder.DecodeType.Audio
}