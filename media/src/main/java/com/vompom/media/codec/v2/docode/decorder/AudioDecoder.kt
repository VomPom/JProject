package com.vompom.media.codec.v2.docode.decorder

import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaCodec
import android.media.MediaFormat
import com.vompom.media.codec.v2.docode.model.Asset
import com.vompom.media.codec.v2.docode.model.SampleState
import com.vompom.media.codec.v2.utils.VLog
import com.vompom.media.codec.v2.utils.usToS
import java.nio.ByteBuffer

/**
 *
 * Created by @juliswang on 2025/09/24 21:43
 *
 * @Description
 */

class AudioDecoder(asset: Asset) : BaseDecoder(asset) {
    private lateinit var audioTrack: AudioTrack
    private var currentPlayPositionUs = 0L
    private var cnt = 0
    override fun render(buffer: ByteBuffer?, bufferInfo: MediaCodec.BufferInfo) {
        if (buffer != null) {
            audioTrack.write(buffer, bufferInfo.size, AudioTrack.WRITE_BLOCKING)
            currentPlayPositionUs = bufferInfo.presentationTimeUs
            cnt++
            VLog.v("audio pts:${usToS(bufferInfo.presentationTimeUs)}s size:${bufferInfo.size} offset: ${bufferInfo.offset} cnt: $cnt")
        }
    }

    override fun configure(codec: MediaCodec) {
        codec.configure(extractor.getMediaFormat(), null, null, 0)
    }

    override fun onPrepare() {
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
            AudioFormat.CHANNEL_OUT_STEREO  // 双声道
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

    override fun readSample(targetTimeUs: Long): SampleState {
        if (isReleased) {
            return SampleState()
        }
        // 向 MediaCodec 添加解码的数据，在没有 EOS 之前一直添加
        if (!readSampleDone) {
            doReadSample()
        }

        // 从 MediaCodec 队列中获取解码后的数据
        if (!isDecodeDone) {
            return renderBuffer(true)
        }
        return SampleState()
    }

    override fun seek(timeUs: Long) {
        super.seek(timeUs)
        currentPlayPositionUs = timeUs
    }

    override fun getCurrentPlayUs(): Long = currentPlayPositionUs

    override fun release() {
        super.release()
        audioTrack.release()
    }

    override fun decodeType(): IDecoder.DecodeType = IDecoder.DecodeType.Audio
}