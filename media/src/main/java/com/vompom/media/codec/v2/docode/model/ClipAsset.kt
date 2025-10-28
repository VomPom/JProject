package com.vompom.media.codec.v2.docode.model

/**
 *
 * Created by @juliswang on 2025/10/24 20:47
 *
 * @Description 包含时间资源时间区间的 Asset 对象
 */

class ClipAsset(path: String, val sourceRange: TimeRange) : Asset(path) {

    init {
        checkRange()
    }

    fun checkRange() {
        if (sourceRange.durationUs > sourceDurationUs) {
            sourceRange.durationUs = sourceDurationUs
        }
        if (sourceRange.startUs <= 0) {
            sourceRange.startUs = 0
        }
    }
}