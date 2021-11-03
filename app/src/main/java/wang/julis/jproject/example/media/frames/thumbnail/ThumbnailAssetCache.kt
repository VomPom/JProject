package com.tencent.tavcut.thumbnail

import android.graphics.Bitmap
import android.util.LruCache
import java.util.Objects

/**
 * @author: mathewchen
 * e-mail : mathewchen@tencent.com
 * time   : 2021/03/05
 * desc   : 以资源ID为管理维度的缓存
 * version: 2.0
 */
internal class ThumbnailAssetCache(
    val assetPath: String,
    val lruCache: LruCache<LRUKey, Int>
)

internal data class BitmapPoint(val bitmap: Bitmap?, val time: Long)

internal data class LRUKey(val assetPath: String, val time: Long) {
    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || javaClass != other.javaClass) {
            return false
        }
        val lruKey = other as LRUKey
        return time == lruKey.time && assetPath == lruKey.assetPath
    }

    override fun hashCode(): Int {
        return Objects.hash(assetPath, time)
    }

}