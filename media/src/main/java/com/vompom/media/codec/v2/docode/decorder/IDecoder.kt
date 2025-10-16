package com.vompom.media.codec.v2.docode.decorder

/**
 *
 * Created by @juliswang on 2025/09/24 17:33
 *
 * @Description
 */

interface IDecoder {
    enum class DecodeType {
        Video,
        Audio
    }

    fun prepare()
    fun release()
    fun readSample(targetTime: Long)
    fun seek(timeUs: Long)
    fun getCurrentPlayUs(): Long
    fun setProgressListener(onProgress: (Long, Long) -> Unit)
}