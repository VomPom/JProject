package com.vompom.media.codec.v2

import android.media.MediaExtractor
import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 *
 * Created by @juliswang on 2025/09/24 16:56
 *
 * @Description 媒体资源获取器
 */

class AssetExtractor : IExtractor {
    private var extractor = MediaExtractor()
    private var trackIndex = -1
    private var currentSampleTime = 0L
    private var currentSampleFlags = 0
    private var mediaFormat: MediaFormat? = null

    override fun setDataSource(path: String) {
        extractor.setDataSource(path)
    }

    override fun readSampleData(byteBuffer: ByteBuffer): Int {
        val size = extractor.readSampleData(byteBuffer, 0)
        currentSampleTime = extractor.sampleTime
        currentSampleFlags = extractor.sampleFlags
        VLog.i("readSampleData size: $size")
        extractor.advance()
        return size
    }


    override fun stop() {
        extractor.release()
    }

    override fun seek(timeUs: Long) {
        // todo:: seek with accurate position...
        extractor.seekTo(timeUs, MediaExtractor.SEEK_TO_PREVIOUS_SYNC)
    }

    override fun selectTrack(index: Int) {
        if (index < 0) {
            throw Exception("media track index is invalid.")
        }
        extractor.selectTrack(index)
        trackIndex = index
    }

    fun getTrackIndex(): Int = trackIndex

    override fun getSampleTime(): Long = currentSampleTime

    override fun getSampleFlags(): Int = currentSampleFlags

    override fun getMediaFormat(): MediaFormat {
        if (mediaFormat == null) {
            mediaFormat = extractor.getTrackFormat(trackIndex)
        }
        return mediaFormat!!
    }

    override fun findTrack(target: String): Int {
        for (index in 0 until extractor.trackCount) {
            val format: MediaFormat = extractor.getTrackFormat(index)
            val mimeType = format.getString(MediaFormat.KEY_MIME) ?: ""
            if (mimeType.startsWith(target)) {
                return index
            }
        }
        return -1
    }

}