package com.tencent.tavcut.thumbnail

import android.graphics.Bitmap
import android.util.LruCache
import com.tencent.logger.Logger
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Logger

/**
 * @author: mathewchen
 * e-mail : mathewchen@tencent.com
 * time   : 2020/05/13
 * desc   : 修改成子provider
 * version: 2.0
 */
internal class ThumbnailAssetProvider(
    val groupId: String,
    val resourceModel: ThumbAssetModel,
    lruCache: LruCache<LRUKey, Int>,
    handler: IRunnableHandler,
    coverWidth: Int,
    coverHeight: Int,
) {
    /**
     * 封面生成监听
     */
    private val defaultVideoThumbListener = IThumbnailGeneratedListener { _, startTimeMs, bitmap ->
        bitmap?.let {
            cacheHelper.addThumbnail(startTimeMs, it)
        }
        val key: String = startTimeMs.toString()
        val list: List<Any>? = tagTable[key]
        list?.let { tags ->
            tagTable.remove(key)
            userVideoThumbListener?.let { listener ->
                tags.forEach {
                    listener.onThumbnailGenerated(it, startTimeMs, bitmap)
                }
            }
        }
    }

    private var userVideoThumbListener: IThumbnailGeneratedListener? = null

    /**
     * 封面缓存
     */
    private val cacheHelper: ICacheInterface

    private val cache: ThumbnailAssetCache

    private var defaultBitmap: Bitmap? = null
    private val sourceTimeDuration
        get() = resourceModel.sourceTimeDurationUs
    private val tagTable =
        ConcurrentHashMap<String, CopyOnWriteArrayList<Any>>()

    /**
     * 对象引用计数
     */
    private val refCount = AtomicInteger(0)

    private val generatorHelper: IThumbnailGenerator

    private val generator: ThumbnailGeneratorSupplier

    val cacheSize: Long
        get() = cacheHelper.getCacheSize()

    val assetPath
        get() = resourceModel.assetPath


    init {
        val assetCache = ThumbnailAssetCache(resourceModel.assetPath, lruCache)
        cacheHelper = ThumbnailVideoCacheHelper(assetCache)
        cache = assetCache

        generator =
            ThumbnailGeneratorSupplier(defaultVideoThumbListener, coverHeight, coverWidth, handler)

        generatorHelper = ThumbnailGeneratorHelper(generator, resourceModel)
    }

    fun setDefaultBitmap(bitmap: Bitmap?) {
        defaultBitmap = bitmap
    }

    /**
     * 设置封面生成监听
     */
    fun setThumbnailListener(videoThumbListener: IThumbnailGeneratedListener?) {
        userVideoThumbListener = videoThumbListener
    }

    /**
     * 暂停截图
     */
    fun pause() {
        generatorHelper.pause()
    }

    /**
     * 继续截图
     */
    fun resume() {
        generatorHelper.resume()
    }

    private fun getBitmapByNearbyTime(nearbyTime: Long, tag: Any?): Bitmap? {
        //虽然查找到了标准点时间，但是这个时候不一定有对应的图片生成，先查找看看有没有对应的
        val bitmapPoint = cacheHelper.getThumbnail(nearbyTime)
        if (bitmapPoint?.bitmap == null) {
            sendGenerateRequest(tag, nearbyTime)
            return defaultBitmap
        }
        if (bitmapPoint.time != nearbyTime) {
            //发送请求
            sendGenerateRequest(tag, nearbyTime)
        }
        return bitmapPoint.bitmap
    }

    fun getBitmapByAbsoluteTime(timeUs: Long, tag: Any?): Bitmap? {
        //找到最近一个标准点的图片时间,进来已经是相对时间了，相对该视频mSourceTimeStart的偏移时间
        //竞品竟然是一秒一张图，所以不跟mScaleAdapter动态设置
        return if (resourceModel.type == TYPE_IMAGE) {
            //图片无需计算时间，直接取0
            getBitmapByNearbyTime(0, tag)
        } else {
            val normalizedTime = getNormalizedSeekTime(
                timeUs,
                sourceTimeDuration
            )
            getBitmapByNearbyTime(normalizedTime, tag)
        }
    }

    private fun sendGenerateRequest(tag: Any?, timeMs: Long) {

        //将时间和TAG相关联，最后给外部回调
        val key: String = timeMs.toString()
        tagTable[key]?.let {
            if (it.contains(tag).not()) {
                it.add(tag)
            }
            return
        }

        val list = CopyOnWriteArrayList<Any>()
        list.add(tag)
        tagTable[key] = list
        Logger.i(TAG, "sendGenerateRequest time:$timeMs,Object:$tag")
        generatorHelper.generateThumbnailByTime(timeMs)
    }

    private fun getNormalizedSeekTime(timeUs: Long, duration: Long): Long {
        //获取标准化的时间，现在是一秒一张
        var secCount = timeUs / ONE_SEC
        //四舍五入
        if (timeUs % ONE_SEC >= ONE_SEC / 2) {
            secCount++
        }
        var normalizeTime = secCount * ONE_SEC
        if (normalizeTime < 0) {
            normalizeTime = 0
        }
        if (normalizeTime > duration) {
            //严丝合缝取最后一帧会花屏，所以往前退100毫秒
            normalizeTime = duration - END_OFFSET
        }
        return normalizeTime
    }

    /**
     * 销毁资源
     */
    fun release() {
        Logger.i(TAG, "release:${resourceModel.assetPath}")
        generatorHelper.release()
        cacheHelper.release()
    }

    fun releaseBitmap(time: Long) {
        cacheHelper.releaseLowMemory(time)
    }

    fun refIncrease() {
        refCount.getAndIncrement()
    }

    fun refDecrease() {
        refCount.getAndDecrement()
    }

    fun canRelease(): Boolean {
        return refCount.get() <= 0
    }

    companion object {
        private const val TAG = "VideoThumbAssetProvider"
        private const val ONE_SEC = 1000_000
        private const val END_OFFSET = 100_000
    }

}