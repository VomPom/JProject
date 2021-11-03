package com.tencent.tavcut.thumbnail

import android.graphics.Bitmap
import com.tencent.logger.Logger
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

/**
 * @author: mathewchen
 * e-mail : mathewchen@tencent.com
 * time   : 2021/03/05
 * desc   : 视频缓存
 * version: 1.0
 */
private const val TAG = "ThumbnailVideoCacheHelper"

internal class ThumbnailVideoCacheHelper(
    private val cache: ThumbnailAssetCache
) : ICacheInterface {
    /**
     * 顺序记录时间点
     */
    internal val bitmapList = CopyOnWriteArrayList<BitmapPoint>()

    /**
     * 缓存index所在的视频片段的封面
     */
    override fun addThumbnail(startTimeUs: Long, bitmap: Bitmap) {

        Logger.i(TAG, "addCover: $startTimeUs")
        val bitmapPoint = BitmapPoint(bitmap, startTimeUs)
        bitmapList.run {
            add(bitmapPoint)
            val newList = sortedBy {
                it.time
            }
            clear()
            addAll(newList)
        }
        cache.lruCache.put(LRUKey(cache.assetPath, startTimeUs), 0)
    }

    /**
     * 获取index所在的视频片段的封面
     */
    override fun getThumbnail(timeUs: Long): BitmapPoint? {
        val findItem = bitmapList.minByOrNull {
            // 找到与所取时间点最近的图片
            abs(it.time - timeUs)
        } ?: return null

        cache.lruCache[LRUKey(cache.assetPath, timeUs)]
        return findItem
    }

    override fun releaseLowMemory(timeUs: Long) {
        bitmapList.find { it.time == timeUs }?.let { bitmapPoint ->
            bitmapList.remove(bitmapPoint)
            bitmapPoint.bitmap?.let { bitmap ->
                if (bitmap.isRecycled.not()) {
                    Logger.i(TAG, "bitmap recycle:" + bitmapPoint.time)
                    bitmap.recycle()
                }
            }
        }
    }

    override fun release() {
        bitmapList.forEach { point ->
            point.bitmap?.let {
                if (it.isRecycled.not()) {
                    it.recycle()
                }
            }
            cache.lruCache.remove(LRUKey(cache.assetPath, point.time))
        }

        bitmapList.clear()
    }

    override fun getCacheSize(): Long {
        return bitmapList.sumOf { it.bitmap?.allocationByteCount ?: 0 }.toLong()
    }
}