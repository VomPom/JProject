package com.vompom.media.codec.v2.extractor

import android.media.MediaFormat
import java.nio.ByteBuffer

/**
 *
 * Created by @juliswang on 2025/09/24 16:59
 *
 * @Description
 */

interface IExtractor {
    fun setDataSource(path: String)
    fun readSampleData(byteBuffer: ByteBuffer): Int
    fun getSampleTime(): Long
    fun getSampleFlags(): Int
    fun stop()
    fun seek(pos: Long): Long
    fun selectTrack(index: Int)
    fun getMediaFormat(): MediaFormat
    fun getMediaFormats(): List<MediaFormat>
    fun findTrack(mimeType: String): Int
    fun duration(): Long
}