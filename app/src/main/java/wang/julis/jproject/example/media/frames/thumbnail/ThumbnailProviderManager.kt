package com.tencent.tavcut.thumbnail

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Handler
import android.os.HandlerThread
import android.os.Process
import android.util.LruCache
import com.tencent.logger.Logger
import java.util.logging.Logger
import kotlin.math.roundToInt

/**
 * @author: mathewchen
 * e-mail : mathewchen@tencent.com
 * time   : 2021/03/05
 * desc   : 缩略图管理者
 * version: 2.0
 */
object ThumbnailProviderManager {
    private const val TAG = "VideoThumbProviderManager"

    /**
     * 最大缓存图片大小，20M
     * 120*120生成图片不到300kb，20M能存68张，以一秒一张来算
     * 一分钟视频从头滑到尾不需要再生成
     */
    private const val MAX_SIZE = 20 * 1024
    private const val B_TO_KB = 1024
    private const val DEFAULT_WIDTH = 120f
    private const val DEFAULT_HEIGHT = 120f
    private const val DEFAULT_BITMAP_COLOR = "#4dffffff"

    private val videoThumbAssetProviders = mutableListOf<ThumbnailAssetProvider>()
    private var defaultBitmap: Bitmap? = null
    private var width: Int
    private var height: Int
    private val videoThumbListeners = mutableListOf<IThumbnailGeneratedListener>()
    private val subVideoThumbListener = IThumbnailGeneratedListener { tag, startTimeMs, bitmap ->
        videoThumbListeners.forEach { it.onThumbnailGenerated(tag, startTimeMs, bitmap) }
    }

    private val groupAssetIdMapping = mutableMapOf<String, List<String>>()

    /**
     * 记录图片时间节点
     */
    private val lruCache = VideoThumbLruCache(MAX_SIZE)
    private val generateHandler by lazy {
        //创建一个专门用来生成缩略图的线程，因为缩略图的生成和UI相关,所以优先级不能太低
        val handlerThread = HandlerThread(
            TAG,
            Process.THREAD_PRIORITY_DEFAULT
        )
        handlerThread.start()
        Handler(handlerThread.looper)
    }
    private val runnableHandler: IRunnableHandler = object : IRunnableHandler {
        override fun postRunnable(runnable: Runnable?) {
            //给新来的任务让路
            generateHandler.postAtFrontOfQueue(runnable)
        }

        override fun runnableFailAndReleaseCache() {
            //打印当前Cache还有Size
            val cacheSize = cacheSize
            val providerSize = videoThumbAssetProviders.size
            //释放一半内存
            lruCache.trimToSize(lruCache.maxSize() / 2)
            Logger.e(
                TAG,
                "runnableFailAndReleaseCache currentCacheSize:$cacheSize,size:$providerSize"
            )
        }
    }

    private class VideoThumbLruCache constructor(maxSize: Int) :
        LruCache<LRUKey, Int>(maxSize) {
        override fun sizeOf(key: LRUKey, value: Int): Int {
            //转化KB
            return value / B_TO_KB
        }

        override fun entryRemoved(
            evicted: Boolean, key: LRUKey,
            oldValue: Int, newValue: Int?
        ) {
            super.entryRemoved(evicted, key, oldValue, newValue)
            //替换也会调用该方法
            if (!evicted) {
                return
            }
            videoThumbAssetProviders.find {
                key.assetPath == it.assetPath
            }?.releaseBitmap(key.time)
        }
    }

    fun initWidthAndHeight(width: Int, height: Int) {
        this.width = width
        this.height = height
        Logger.i(TAG, "width:$width,height:$height")
    }

    /**
     * 注意,reset代表全量替换，如果是单独场景请用ADD接口自行管理生命周期
     *
     * @param assetModelList assetList
     * @param groupId 建议用页面ID
     */
    fun reset(assetModelList: List<ThumbAssetModel>, groupId: String) {
        if (width != 0 && height != 0) {
            resetProvider(assetModelList, groupId)
        }
    }

    fun addProvider(assetModelList: List<ThumbAssetModel>, groupId: String) {
        //这里要做差异化处理
        addProviderIfNeed(assetModelList, groupId) {
            it.refIncrease()
        }
    }

    private fun resetProvider(assetModelList: List<ThumbAssetModel>, groupId: String) {
        Logger.i(TAG, "resetProvider start")
        addProviderIfNeed(assetModelList, groupId, null)
        val temp = assetModelList.map { it.assetPath }
        releaseGroup(videoThumbAssetProviders, groupId, temp)
        groupAssetIdMapping.remove(groupId)
        groupAssetIdMapping[groupId] = temp
    }

    private fun addProviderIfNeed(
        assetModelList: List<ThumbAssetModel>, groupId: String,
        findCallback: ((ThumbnailAssetProvider) -> Unit)?
    ) {
        assetModelList.forEach { assetModel ->
            val assetPath: String = assetModel.assetPath
            //总共就没几个素材，双层循环搞定
            val oldProvider = videoThumbAssetProviders.find { assetPath == it.assetPath }
            //如果找不到存在的就新增
            if (oldProvider == null) {
                val newProvider = crateProvider(groupId, assetModel, assetPath)
                videoThumbAssetProviders.add(newProvider)
            } else {
                Logger.i(TAG, "resetProvider find:$assetPath")
                findCallback?.invoke(oldProvider)
            }
        }
    }


    private fun crateProvider(
        groupId: String, resourceModel: ThumbAssetModel,
        assetPath: String
    ): ThumbnailAssetProvider {
        Logger.i(TAG, "resetProvider can't find:$assetPath")

        return ThumbnailAssetProvider(
            groupId = groupId,
            resourceModel = resourceModel,
            lruCache = lruCache,
            handler = runnableHandler,
            coverWidth = width,
            coverHeight = height
        ).apply {
            setThumbnailListener(subVideoThumbListener)
            setDefaultBitmap(defaultBitmap)
        }
    }

    private fun releaseGroup(
        sourceList: MutableList<ThumbnailAssetProvider>, groupId: String,
        exceptList: List<String>
    ) {
        sourceList.filter {
            //找到需要释放的group且不包含在例外列表中
            it.groupId == groupId && exceptList.contains(it.assetPath).not()
        }.forEach {
            //从列表中移除
            if (it.canRelease()) {
                Logger.i(TAG, "release provider:" + it.assetPath)
                sourceList.remove(it)
                it.release()
            } else {
                Logger.i(TAG, "can't release provider:" + it.assetPath)
            }
        }
    }

    /**
     * 从指定的缓存中捞数据
     *
     * @param timeMs 时间
     * @param tag tag会在回调中回传
     * @param assetPath 资源路径
     * @return 返回值
     */
    fun getBitmapByTime(timeMs: Long, tag: Any?, assetPath: String?): Bitmap? {
        if (height == 0 && width == 0) {
            return defaultBitmap
        }
        //看看time命中了哪个截图器
        if (videoThumbAssetProviders.isEmpty()) {
            return defaultBitmap
        }
        //指定取指定ID的资源
        val provider = videoThumbAssetProviders.find {
            assetPath == it.assetPath
        } ?: return defaultBitmap


        //没有命中返回默认图
        return provider.getBitmapByAbsoluteTime(timeMs, tag)
    }

    fun registerListener(listener: IThumbnailGeneratedListener) {
        if (videoThumbListeners.contains(listener).not()) {
            videoThumbListeners.add(listener)
            Logger.i(TAG, "registerListener addSuccess")
        }
    }

    fun unRegisterListener(listener: IThumbnailGeneratedListener) {
        val result = videoThumbListeners.remove(listener)
        Logger.i(TAG, "unRegisterListener removeSuccess:$result")
    }

    fun resume() {
        videoThumbAssetProviders.forEach { it.resume() }
        Logger.i(TAG, "resume")
    }

    fun pause() {
        videoThumbAssetProviders.forEach { it.pause() }
        Logger.i(TAG, "pause")
    }

    fun release(groupId: String) {
        releaseGroup(videoThumbAssetProviders, groupId, emptyList())
        Logger.i(TAG, "release:$groupId")
    }

    private val cacheSize: Int
        get() {
            return videoThumbAssetProviders.sumBy {
                it.cacheSize.toInt()
            }
        }

    fun setDefaultBitmap(bitmap: Bitmap) {
        defaultBitmap = bitmap
        videoThumbAssetProviders.forEach { it.setDefaultBitmap(bitmap) }
    }

    fun getDefaultBitmap(): Bitmap? {
        return defaultBitmap
    }

    fun releaseProviderByAssetPath(assetPath: String) {
        Logger.i(TAG, "releaseProviderByAssetPath:$assetPath")
        val provider = videoThumbAssetProviders.find { assetPath == it.assetPath }
        if (provider != null) {
            // 引用-1
            provider.refDecrease()
            // 如果引用归0且没有Group引用，则可以释放
            val findResult = groupAssetIdMapping.entries.any {
                it.value.contains(assetPath)
            }
            if (provider.canRelease() && findResult.not()) {
                Logger.i(TAG, "releaseProviderByAssetPath success")
                videoThumbAssetProviders.remove(provider)
                provider.release()
            } else {
                Logger.i(TAG, "releaseProviderByAssetPath assetId is still used")
            }
        }
    }


    init {
        width = dp2px(DEFAULT_WIDTH)
        height = dp2px(DEFAULT_HEIGHT)
        val bitmap = Bitmap.createBitmap(
            width, height,
            Bitmap.Config.ARGB_8888
        )

        bitmap.eraseColor(Color.parseColor(DEFAULT_BITMAP_COLOR))
        setDefaultBitmap(bitmap)
    }

    private fun dp2px(dpValue: Float): Int {
        val scale = Resources.getSystem().displayMetrics.density
        // 像素没有小数；此处+0.5是为了解决向上取整，防止非整型的dp数被int取整后丢失精度
        // e.g. 1.5dp在3x的手机上应该按5px处理
        return (dpValue * scale).roundToInt()
    }
}