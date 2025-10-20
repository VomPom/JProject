package com.vompom.media.codec.v2.docode.model

/**
 *
 * Created by @juliswang on 2025/10/10 18:44
 *
 * @Description
 */

class TrackSegment(val asset: Asset) {
    var starUs = 0L

    fun startUs(): Long {
        return starUs
    }

    fun setStartUs(timeUs: Long) {
        this.starUs = timeUs
    }

    fun durationUs(): Long = asset.durationUs
}