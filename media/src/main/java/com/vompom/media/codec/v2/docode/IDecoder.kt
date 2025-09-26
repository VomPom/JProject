package com.vompom.media.codec.v2.docode

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

    fun play()
    fun pause()
    fun stop()
    fun release()
    fun seek(timeUs: Long)
}