package com.vompom.media.codec.v2.docode.decorder

/**
 *
 * Created by @juliswang on 2025/09/25 10:35
 *
 * @Description
 */

enum class DecodeState {
    START,

    DECODING,

    PAUSE,

    SEEKING,

    FINISH,

    STOP,
}