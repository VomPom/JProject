package com.vompom.media.codec.v2.docode.model

import com.vompom.media.codec.v2.docode.decorder.IDecoder

/**
 *
 * Created by @juliswang on 2025/10/20 16:14
 *
 * @Description
 */
class SampleState {
    constructor() : this(0)

    constructor(frameTimeUs: Long, state: Int = IDecoder.SAMPLE_STATE_NORMAL) {
        this.frameTimeUs = frameTimeUs
        this.stateCode = state
    }


    var frameTimeUs: Long = 0
    var stateCode = 0
    override fun toString(): String {
        return "[timeUs:$frameTimeUs, state:$stateCode]"
    }
}
