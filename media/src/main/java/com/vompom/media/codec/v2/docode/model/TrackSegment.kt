package com.vompom.media.codec.v2.docode.model

/**
 *
 * Created by @juliswang on 2025/10/10 18:44
 *
 * @Description 在轨道上的数据对象
 */

class TrackSegment(val asset: ClipAsset) {
    private var timelineStartUs: Long = 0L

    val sourceRange: TimeRange = asset.sourceRange

    val timelineRange: TimeRange = TimeRange(timelineStartUs, asset.sourceRange.durationUs)
}