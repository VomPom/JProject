package com.vompom.media.codec.v2.docode.decorder

import android.media.MediaCodec
import android.media.MediaFormat
import com.vompom.media.codec.v2.extractor.AssetExtractor
import com.vompom.media.codec.v2.utils.VLog
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

abstract class BaseDecoder : IDecoder {
    companion object {
        const val TIME_US: Int = 10000
    }

    private var sourcePath = ""

    // 解码后的数据信息
    private var bufferInfo = MediaCodec.BufferInfo()

    val extractor: AssetExtractor = AssetExtractor()
    lateinit var mediaCodec: MediaCodec

    private var onProgress: ((Long, Long) -> Unit)? = null

    var isDecodeDone = false
    var isEOS = false
    var readSampleDone = false

    constructor(path: String) {
        this.sourcePath = path
    }

    override fun prepare() {
        initExtractor(sourcePath)
        initCodec()
        onPrepare()
    }

    private fun initExtractor(sourcePath: String) {
        extractor.apply {
            setDataSource(sourcePath)
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
    fun fillBufferToDecoder(): Long {
        try {
            // 获取一个 input buffer index, 延迟 TIME_US 等待拿到空的 input buffer下标，单位为 us
            // -1 表示一直等待，直到拿到数据，0 表示立即返回
            val inputBufferId = mediaCodec.dequeueInputBuffer(TIME_US.toLong())
            if (inputBufferId >= 0) {
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
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return extractor.getSampleTime()
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
    fun fetchBufferFromDecoder(): Long {
        var bufferTime = 0L
        // Decoder 在任何一个时机都有可能会执行 release 操作，但这里的 dequeueOutputBuffer,release 还没有执行完成
        // 当 MediaCodec 被回收之后，再执行到这里可能会报：java.lang.IllegalStateException，需加一个 try cache
        try {
            // 获取解码后的数据，数据将会输入到 bufferInfo 里面
            var outputIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIME_US.toLong())
            val outputBuffer: ByteBuffer?
            if (outputIndex >= 0) {
                outputBuffer = mediaCodec.getOutputBuffer(outputIndex)
                bufferTime = bufferInfo.presentationTimeUs
                render(outputBuffer, bufferInfo)
                mediaCodec.releaseOutputBuffer(outputIndex, true)
            } else {
                when (outputIndex) {
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {}
                    MediaCodec.INFO_TRY_AGAIN_LATER -> {}
                    else -> {
                        // no-op
                    }
                }
            }
        } catch (_: Exception) {
            // no-op
        }
        if (bufferInfo.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
            isDecodeDone = true
            onLoop()
        }
        return bufferTime
    }


    override fun seek(timeUs: Long) {
        // todo:: fix timestamp calculate after seek
        val sampleTimeUs = extractor.seek(timeUs)
        VLog.d("--julis seek sampleTimeUs $sampleTimeUs")
    }


    override fun setProgressListener(onProgress: (Long, Long) -> Unit) {
        this.onProgress = onProgress
    }


    override fun release() {
        try {
            extractor.stop()
            mediaCodec.stop()
            mediaCodec.release()
        } catch (e: Exception) {
            //no-op
        }
    }

    private fun onLoop() {
        isEOS = false
        isDecodeDone = false
        // todo:: set video asset range start...
        seek(0)

        // MediaCodec.flush() 作用
        // 清空缓冲区：丢弃所有当前在编解码器内部排队（已 queue 但尚未处理）的输入缓冲区数据和已解码但尚未取出的输出缓冲区数据
        // 保持状态：编解码器保持在当前状态（如 Started 状态）
        // 立即生效：调用后立即清空缓冲区
        mediaCodec.flush()

        // 循环播放的话还需要将 bufferInfo 的 flag 重制为 0，避开对 MediaCodec.BUFFER_FLAG_END_OF_STREAM 的逻辑判断
        bufferInfo.flags = 0

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
    abstract fun onPrepare()

    abstract fun decodeType(): IDecoder.DecodeType
}