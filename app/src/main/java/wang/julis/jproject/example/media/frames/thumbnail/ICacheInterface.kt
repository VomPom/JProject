package com.tencent.tavcut.thumbnail

import android.graphics.Bitmap

/**
 *
 * @author: mathewchen
 * e-mail : mathewchen@tencent.com
 * time   : 2021/03/08
 * desc   :
 * version: 1.0
 *
 */
internal interface ICacheInterface {
    fun addThumbnail(startTimeUs: Long, bitmap: Bitmap)
    fun getThumbnail(timeUs: Long): BitmapPoint?

    fun releaseLowMemory(timeUs: Long)
    fun release()

    fun getCacheSize():Long
}