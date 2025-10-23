package com.vompom.media.codec.v2.docode.model

/**
 *
 * Created by @juliswang on 2025/10/10 18:44
 *
 * @Description 在 Asset 的区间中的数据对象
 */

class TrackSegment(val asset: Asset) {
    var startUs: Long = 0L

    val durationUs: Long = asset.durationUs
}