package com.tencent.tavcut.thumbnail

import com.tencent.logger.Logger
import com.tencent.tavcut.TavCut
import com.tencent.tavcut.composition.model.component.Size
import com.tencent.tavcut.composition.model.component.TimeRange
import com.tencent.tavcut.model.ClipSource
import com.tencent.tavcut.render.thumb.IThumbProvider
import com.tencent.videocut.utils.BitmapUtil
import com.tencent.videocut.utils.VideoUtils
import com.tencent.videocut.utils.centerInside
import java.util.concurrent.BlockingQueue
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit

/**
 *
 * @author: mathewchen
 * e-mail : mathewchen@tencent.com
 * time   : 2021/03/17
 * desc   : 实现截取视频/图片截图的helper
 * version: 1.0
 *
 */
internal class ThumbnailGeneratorHelper(
    private val generatorSupplier: ThumbnailGeneratorSupplier,
    private val model: ThumbAssetModel
) : IThumbnailGenerator {
    @Volatile
    private var runnable: ImageRunnable? = null

    private val clipSource: ClipSource

    private var thumbProvider: IThumbProvider? = null

    private val size by lazy {
        calculateRenderSize()
    }

    /**
     * 用栈好一些，让先来的先加载
     */
    private val videoThumbTaskQueue: BlockingQueue<ImageThumbTask> = PriorityBlockingQueue()

    init {
        val source = when (model.type) {
            TYPE_IMAGE ->
                TavCut.getClipSourceCreator().createPhotoClipSource(
                    model.assetPath,
                    TimeRange(0, model.sourceTimeDurationUs)
                )
            else -> TavCut.getClipSourceCreator().createVideoClipSource(
                model.assetPath,
                TimeRange(0, model.sourceTimeDurationUs)
            )
        }
        clipSource = source
    }

    override fun generateThumbnailByTime(timeUs: Long) {
        Logger.i(TAG, "generateCoverByTime:$timeUs")
        //生成器不应该关心任务是否重复，只负责生成就好了，provider与cache去保证不会post
        //同样的任务
        val task = ImageThumbTask(System.currentTimeMillis(), timeUs)
        try {
            videoThumbTaskQueue.put(task)
        } catch (e: InterruptedException) {
            Logger.e(TAG, e)
        }
        startGenerate()
    }

    override fun pause() {
        Logger.i(TAG, "pause:")
        //没有正在运行的任务不需要暂停
        if (runnable == null) {
            return
        }
        runnable?.setPause(true)
    }

    override fun resume() {
        Logger.i(TAG, "resume:")
        //如果本来就没有截图任务，那也不需要resume
        if (videoThumbTaskQueue.isEmpty()) {
            return
        }
        startGenerate()
    }

    override fun release() {
        //如果此时有任务在跑，直接停下来，里面会调用资源的释放
        if (runnable != null) {
            runnable?.setRelease(true)
            return
        }
        releaseQueue()
        generatorSupplier.generateHandler.postRunnable(ReleaseRunnable())
    }

    /**
     * 释放任务队列
     */
    private fun releaseQueue() {
        Logger.i(TAG, "releaseQueue:")
        videoThumbTaskQueue.clear()
    }

    private fun startGenerate() {
        //如果有任务正在进行，说明已经resume过了，也不需要再次post
        if (runnable != null) {
            Logger.i(TAG, "mRunnable is run")
            return
        }
        runnable = ImageRunnable()
        generatorSupplier.generateHandler.postRunnable(runnable)
    }

    private fun initThumbGenerator() {
        if (thumbProvider == null) {
            clipSource.let {
                thumbProvider = TavCut.createThumbProvider(it, size)
            }
        }
    }

    private fun releaseThumbGenerator() {
        thumbProvider?.release()
        thumbProvider = null
    }

    private fun calculateRenderSize(): Size {
        val (videoWidth, videoHeight) = if (model.type == TYPE_IMAGE) {
            BitmapUtil.getBitmapWidthAndHeight(clipSource.path)
        } else {
            VideoUtils.getWidthAndHeight(clipSource.path)
        }

        return generatorSupplier.run {
            val result = videoWidth to videoHeight centerInside (thumbnailWidth to thumbnailHeight)
            if (result.width <= 0 || result.height <= 0) {
                Size(thumbnailWidth, thumbnailHeight)
            } else {
                Size(result.width, result.height)
            }
        }
    }

    private inner class ReleaseRunnable : Runnable {
        override fun run() {
            this@ThumbnailGeneratorHelper.releaseThumbGenerator()
        }
    }

    private class ImageThumbTask constructor(
        private val requestTimeMs: Long, val startTimeUs: Long
    ) : Comparable<ImageThumbTask> {

        override fun compareTo(other: ImageThumbTask): Int {
            return (requestTimeMs - other.requestTimeMs).toInt()
        }

    }

    private inner class ImageRunnable : Runnable {
        private var retryCount = 0
        private var isPause = false
        private var isRelease = false
        private var isTimeOut = false
        fun setPause(pause: Boolean) {
            isPause = pause
        }

        fun setRelease(release: Boolean) {
            isRelease = release
        }

        override fun run() {
            initThumbGenerator()
            while (checkLoop()) {
                val task = obtainTask()

                if (task == null) {
                    isTimeOut = true
                    break
                }

                val bitmap = thumbProvider?.getThumbAtTime(task.startTimeUs)
                if (bitmap != null) {
                    generatorSupplier.generatedListener.onThumbnailGenerated(
                        null,
                        task.startTimeUs,
                        bitmap
                    )
                } else {
                    try {
                        if (retryCount > MAX_RETRY_COUNT) {
                            continue
                        }
                        retryCount++
                        Logger.i(TAG, "run: retry is $retryCount")
                        videoThumbTaskQueue.put(task)
                    } catch (e: InterruptedException) {
                        Logger.e(TAG, e)
                    }
                }
            }
            runnableFinish()
        }

        private fun obtainTask(): ImageThumbTask? {
            var task: ImageThumbTask? = null
            try {
                task = videoThumbTaskQueue.poll(
                    TIME_OUT_MS,
                    TimeUnit.MILLISECONDS
                )
                Logger.i(TAG, "run: startTime is " + task?.startTimeUs)
            } catch (e: InterruptedException) {
                Logger.e(TAG, e)
            }
            return task
        }

        private fun runnableFinish() {
            //置空
            runnable = null
            releaseThumbGenerator()
            //超时和释放都会释放资源以及清空队列，例如页面已经销毁
            if (isRelease || isTimeOut) {
                releaseQueue()
                return
            }
        }

        private fun checkLoop(): Boolean {
            return ((isPause.not()
                    && isRelease.not()) && !videoThumbTaskQueue.isEmpty())
        }
    }

    companion object {
        private const val MAX_RETRY_COUNT = 3

        /**
         * 超时时间，2000毫秒空转就释放掉资源 防止空转，另一个也是防止快速滑动的时候不停释放
         */
        private const val TIME_OUT_MS = 2000L
        private const val TAG = "VideoThumbImageGenerator"
    }
}