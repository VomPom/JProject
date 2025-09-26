package com.vompom.media.codec.v2.docode

import android.media.MediaCodec
import android.media.MediaFormat
import com.vompom.media.codec.v2.AssetExtractor
import com.vompom.media.codec.v2.VLog
import java.nio.ByteBuffer

/**
 *
 * Created by @juliswang on 2025/09/25 20:10
 *
 * @Description 写一个解码器实现大体流程图：
 *
 *     视频文件 → MediaExtractor → 解码器(MediaCodec) → Surface/AudioTrack → 播放
 *          (分离音视频)      (硬件解码)           (渲染)
 *      具体步骤
 *         - 通过AssetExtractor读取媒体文件
 *         - 管理解码状态（播放、暂停、停止、seeking）
 *         - 读取原始数据到ByteBuffer中
 *         - 将 Buffer 的数据传递到 MediaCodec 中进行解码
 *         - 解码后的数据上屏幕
 *         - 解码后的数据写入AudioTrack中播放
 *         - 音画帧同步
 */

abstract class BaseDecoder(val path: String) : IDecoder, Runnable {
    companion object {
        const val TIME_US: Int = 10000
    }

    private var state = DecodeState.STOP

    // 线程等待锁
    private val decodeLock = Object()

    // 解码后的数据信息
    private var bufferInfo = MediaCodec.BufferInfo()

    // 整体解码开始的时间，后续时间同步以这个时间为基准时间点
    private var startTimeMs = -1L

    val extractor: AssetExtractor = AssetExtractor()
    lateinit var mediaCodec: MediaCodec

    var isDecodeDone = false
    var isRunning = true
    var isEOS = false


    override fun run() {
        initExtractor()
        initCodec()
        onInit()
        setState(DecodeState.START)
        if (startTimeMs <= 0) {
            startTimeMs = System.currentTimeMillis()
        }
        while (isRunning) {
            // 这两种状态需要等待操作完成之后才能进行
            when (state) {
                DecodeState.PAUSE,
                DecodeState.SEEKING -> onWaitDecode()

                else -> {
                    // no-op
                }
            }
            // 任何一次中间流程执行 STOP 则停止整个任务
            if (!isRunning || state == DecodeState.STOP) {
                break
            }

            // 向 MediaCodec 添加解码的数据，在没有 EOS 之前一直添加
            if (!isEOS) {
                fillBufferToDecoder()
            }

            // 从 MediaCodec 队列中获取解码后的数据
            if (!isDecodeDone) {
                fetchBufferFromDecoder()
            }
        }
    }

    private fun initExtractor() {
        extractor.apply {
            setDataSource(path)
            selectTrack(trackIndex())
        }
    }

    private fun initCodec(): Boolean {
        try {
            val type = extractor.getMediaFormat().getString(MediaFormat.KEY_MIME)
            if (type == null) {
                VLog.e("can't get media type.")
            } else {
                mediaCodec = MediaCodec.createDecoderByType(type)
            }
            configure(mediaCodec)
            mediaCodec.start()
        } catch (e: Exception) {
            VLog.e("init codec failed,e:${e.message}")
            return false
        }
        return true
    }

    /**
     * 向 MediaCodec 输入缓冲区队列添加原始数据
     *
     * 作用流程：
     * 1. 从 MediaCodec 获取一个可用的输入缓冲区 ID
     * 2. 通过 ID 获取对应的输入缓冲区 ByteBuffer
     * 3. 从 MediaExtractor 读取原始音视频数据到缓冲区
     * 4. 将填充好数据的缓冲区提交给 MediaCodec 进行解码
     * 5. 如果没有更多数据，发送 EOS (End Of Stream) 标志
     *
     * @return Boolean 是否已经处理完所有的数据 (EOS)
     */
    private fun fillBufferToDecoder() {
        // 获取一个 input buffer index, 延迟 TIME_US 等待拿到空的 input buffer下标，单位为 us
        // -1 表示一直等待，直到拿到数据，0 表示立即返回
        val inputBufferId = mediaCodec.dequeueInputBuffer(TIME_US.toLong())
        if (inputBufferId > 0) {
            val inputBuffer = mediaCodec.getInputBuffer(inputBufferId)
            if (inputBuffer != null) {
                val size = extractor.readSampleData(inputBuffer)

                // 将数据压入解码器输入缓冲
                if (size >= 0) {
                    mediaCodec.queueInputBuffer(
                        inputBufferId,
                        0,
                        size,
                        extractor.getSampleTime(),
                        extractor.getSampleFlags()
                    )
                } else {
                    // 结束,传递 end-of-stream 标志
                    mediaCodec.queueInputBuffer(
                        inputBufferId,
                        0,
                        0,
                        0,
                        MediaCodec.BUFFER_FLAG_END_OF_STREAM
                    )
                    VLog.d("No more buffer: $size")
                    isEOS = true
                }
            }
        }
    }

    /**
     * 从 MediaCodec 输出缓冲区队列获取解码后的数据并进行渲染
     *
     * 作用流程：
     * 1. 通过 MediaCodec.dequeueOutputBuffer 获取可用的输出缓冲区索引，获取结果填充到 bufferInfo
     * 2. 如果获得了有效的输出缓冲区，则调用 render() 方法处理解码数据，并释放该缓冲区
     * 3. 如果返回 MediaCodec 的 INFO_* 常量，说明输出格式变化或暂无数据可读，按需处理
     * 4. 检查 bufferInfo.flags 是否为 BUFFER_FLAG_END_OF_STREAM，标记解码完成
     */
    private fun fetchBufferFromDecoder() {
        // 获取解码后的数据，数据将会输入到 bufferInfo 里面
        var outputIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIME_US.toLong())
        val outputBuffer: ByteBuffer?
        if (outputIndex >= 0) {
            outputBuffer = mediaCodec.getOutputBuffer(outputIndex)
            render(outputBuffer, bufferInfo)
            try {
                // Decoder 在任何一个时机都有可能会执行 release 操作，但这里的 release 还没有执行完成
                // 当 MediaCodec 被回收之后，再执行到这里可能会报：java.lang.IllegalStateException，需加一个 try cache
                mediaCodec.releaseOutputBuffer(outputIndex, true)
            } catch (_: Exception) {
                // no-op
            }
        } else {
            when (outputIndex) {
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {}
                MediaCodec.INFO_TRY_AGAIN_LATER -> {}
                else -> {
                    // no-op
                }
            }
        }
        if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
            state = DecodeState.FINISH
            isDecodeDone = true
        }

        syncAudioVideo(bufferInfo.presentationTimeUs, startTimeMs)
    }

    override fun play() {
        setState(DecodeState.DECODING)
        notifyDecode()
    }

    override fun pause() {
        setState(DecodeState.PAUSE)
    }

    override fun stop() {
        setState(DecodeState.STOP)
        isRunning = false

        // 这里也需要调用一次 notifyDecode，如果先调用 pause， 但没有 notifyDecode()，那么：
        // state 被设置为 STOP
        // isRunning 被设置为 false
        // 但是线程仍然在 decodeLock.wait() 处等待，永远不会被唤醒
        // 导致线程无法正常退出，造成线程泄漏
        notifyDecode()
    }

    override fun seek(timeUs: Long) {
        // todo:: seek...
        setState(DecodeState.SEEKING)
        extractor.seek(timeUs)
        notifyDecode()
    }

    private fun setState(state: DecodeState) {
        this.state = state
        VLog.d("DecodeState: ${state.name}")
    }

    override fun release() {
        stop()
        try {
            extractor.stop()
            mediaCodec.stop()
            mediaCodec.release()
        } catch (e: Exception) {
            //no-op
        }
    }

    /**
     * 进行音画同步处理
     * @param ptsUs     解码帧的展示时间（也就是视频播放了多久的时间）
     * @param startMs   播放开始的时间戳
     */
    private fun syncAudioVideo(ptsUs: Long, startMs: Long) {
        val systemTimeDiff = System.currentTimeMillis() - startMs
        val playTimeDiff: Long = ptsUs / 1000 - systemTimeDiff

        // 如果当前帧比系统时间差快了，则延时以下
        if (playTimeDiff > 0) {
            try {
                Thread.sleep(playTimeDiff)
                VLog.d("sleep: $playTimeDiff")
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun onWaitDecode() {
        try {
            synchronized(decodeLock) {
                decodeLock.wait()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun notifyDecode() {
        try {
            synchronized(decodeLock) {
                decodeLock.notify()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun trackIndex(): Int {
        val tagPrefix = when (decodeType()) {
            IDecoder.DecodeType.Video -> "video/"
            IDecoder.DecodeType.Audio -> "audio/"
        }
        return extractor.findTrack(tagPrefix)
    }

    /**
     * 获取到的原始的音视频 buffer 的每一帧数据和相关的信息
     * @param buffer        原始的帧数据
     * @param bufferInfo    帧数据相关的
     */
    abstract fun render(buffer: ByteBuffer?, bufferInfo: MediaCodec.BufferInfo)

    /**
     * 配置 MediaCodec
     */
    abstract fun configure(codec: MediaCodec)

    /**
     * 在 MediaCodec、Extractor 配置完之后供子类 做一些初始化操作
     */
    abstract fun onInit()

    abstract fun decodeType(): IDecoder.DecodeType
}