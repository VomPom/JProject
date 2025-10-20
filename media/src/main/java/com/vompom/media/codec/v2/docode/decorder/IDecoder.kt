package com.vompom.media.codec.v2.docode.decorder

import com.vompom.media.codec.v2.docode.model.SampleState

/**
 *
 * Created by @juliswang on 2025/09/24 17:33
 *
 * @Description
 */

interface IDecoder {
    companion object {
        /**
         * 普通帧
         */
        const val SAMPLE_STATE_NORMAL: Int = 0

        /**
         * 解码完成
         */
        const val SAMPLE_STATE_FINISH: Int = -1

        /**
         * 解码失败
         */
        const val SAMPLE_STATE_FAILED: Int = -2

        /**
         * 其他错误，包括illState、CodecException等
         */
        const val SAMPLE_STATE_ERROR: Int = -3

        /**
         * 解码超时
         */
        const val SAMPLE_STATE_TIMEOUT: Int = -4
    }

    enum class DecodeType {
        Video,
        Audio
    }

    fun prepare()
    fun release()
    fun readSample(targetTime: Long): SampleState
    fun seek(timeUs: Long)
    fun getCurrentPlayUs(): Long
    fun setProgressListener(onProgress: (Long, Long) -> Unit)
}