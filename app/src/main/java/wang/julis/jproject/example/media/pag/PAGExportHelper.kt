package wang.julis.jproject.example.media.pag

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.Environment
import android.util.Log
import org.libpag.PAGFile
import org.libpag.PAGPlayer
import org.libpag.PAGSurface
import java.io.File
import java.io.IOException
import java.nio.ByteBuffer

/**
 * Created by juliswang on 2023/7/5 17:12
 *
 * @Description
 */
class PAGExportHelper(private val pagFile: PAGFile) {
    private lateinit var mBufferInfo: MediaCodec.BufferInfo
    private lateinit var mEncoder: MediaCodec
    private lateinit var mMuxer: MediaMuxer
    private var mTrackIndex = 0
    private var mMuxerStarted = false

    private var pagPlayer: PAGPlayer? = null

    val OUTPUT_DIR = Environment.getExternalStorageDirectory()


    companion object {
        const val MIME_TYPE = "video/avc" // H.264 Advanced Video Coding
        const val FRAME_RATE = 30
        const val IFRAME_INTERVAL = 10 // 10 seconds between I-frames
        const val mBitRate = 8000000
        const val TAG = "PAGExportHelper"
        const val VERBOSE = true
    }

    fun pagExportToMP4() {
        try {
            prepareEncoder(pagFile)
            val totalFrames = (pagFile.duration() * pagFile.frameRate() / 1000000).toInt()
            for (i in 0 until totalFrames) {
                // Feed any pending encoder output into the muxer.
                drainEncoder(false)
                generateSurfaceFrame(i)
                if (VERBOSE) Log.d(
                    TAG,
                    "sending frame $i to encoder"
                )
            }
            drainEncoder(true)
        } catch (e: Exception) {
            Log.e(TAG, "pagExportToMP4 exception:$e")
        } finally {
            releaseEncoder()
        }
        Log.d(TAG, "encode finished!!! \n")
    }

    private fun prepareEncoder(pagFile: PAGFile) {
        mBufferInfo = MediaCodec.BufferInfo()
        var width: Int = pagFile.width()
        var height: Int = pagFile.height()
        if (width % 2 == 1) {
            width--
        }
        if (height % 2 == 1) {
            height--
        }
        val format = MediaFormat.createVideoFormat(
            MIME_TYPE,
            width,
            height
        )
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, mBitRate)
        format.setInteger(
            MediaFormat.KEY_FRAME_RATE,
            FRAME_RATE
        )
        format.setInteger(
            MediaFormat.KEY_I_FRAME_INTERVAL,
            IFRAME_INTERVAL
        )
        try {
            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
        if (pagPlayer == null) {
            val pagSurface = PAGSurface.FromSurface(mEncoder.createInputSurface())
            pagPlayer = PAGPlayer()
            pagPlayer?.surface = pagSurface
            pagPlayer?.composition = pagFile
            pagPlayer?.progress = 0.0
        }
        mEncoder.start()
        val outputPath: String = File(
            OUTPUT_DIR,
            "test." + width + "x" + height + ".mp4"
        ).toString()
        Log.d(
            TAG,
            "video output file is $outputPath"
        )
        try {
            mMuxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        } catch (ioe: IOException) {
            throw RuntimeException("MediaMuxer creation failed", ioe)
        }
        mTrackIndex = -1
        mMuxerStarted = false
    }

    /**
     * Releases encoder resources.  May be called after partial / failed initialization.
     */
    private fun releaseEncoder() {
        mEncoder.stop()
        mEncoder.release()
        mMuxer.stop()
        pagPlayer = null
    }

    private fun drainEncoder(endOfStream: Boolean) {
        val TIMEOUT_USEC = (10000 * 60 / FRAME_RATE) as Int
        if (VERBOSE) Log.d(
            TAG,
            "drainEncoder($endOfStream)"
        )
        if (endOfStream) {
            if (VERBOSE) Log.d(
                TAG,
                "sending EOS to encoder"
            )
            mEncoder.signalEndOfInputStream()
        }
        var encoderOutputBuffers: Array<ByteBuffer?> = mEncoder.getOutputBuffers()
        while (true) {
            val encoderStatus: Int = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_USEC.toLong())
            if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER) {
                // no output available yet
                if (!endOfStream) {
                    break // out of while
                } else {
                    if (VERBOSE) Log.d(
                        TAG,
                        "no output available, spinning to await EOS"
                    )
                }
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
                // not expected for an encoder
                encoderOutputBuffers = mEncoder.getOutputBuffers()
            } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                // should happen before receiving buffers, and should only happen once
                if (mMuxerStarted) {
                    throw RuntimeException("format changed twice")
                }
                val newFormat: MediaFormat = mEncoder.getOutputFormat()
                Log.d(
                    TAG,
                    "encoder output format changed: $newFormat"
                )

                // now that we have the Magic Goodies, start the muxer
                mTrackIndex = mMuxer.addTrack(newFormat)
                mMuxer.start()
                mMuxerStarted = true
            } else if (encoderStatus < 0) {
                Log.w(
                    TAG,
                    "unexpected result from encoder.dequeueOutputBuffer: " +
                        encoderStatus
                )
                // let's ignore it
            } else {
                val encodedData = encoderOutputBuffers[encoderStatus]
                    ?: throw RuntimeException(
                        "encoderOutputBuffer " + encoderStatus +
                            " was null"
                    )
                if (mBufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG != 0) {
                    // The codec config data was pulled out and fed to the muxer when we got
                    // the INFO_OUTPUT_FORMAT_CHANGED status.  Ignore it.
                    if (VERBOSE) Log.d(
                        TAG,
                        "ignoring BUFFER_FLAG_CODEC_CONFIG"
                    )
                    mBufferInfo.size = 0
                }
                if (mBufferInfo.size != 0) {
                    if (!mMuxerStarted) {
                        throw RuntimeException("muxer hasn't started")
                    }
                    // adjust the ByteBuffer values to match BufferInfo (not needed?)
                    encodedData.position(mBufferInfo.offset)
                    encodedData.limit(mBufferInfo.offset + mBufferInfo.size)
                    mMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo)
                    if (VERBOSE) Log.d(
                        TAG,
                        "sent " + mBufferInfo.size + " bytes to muxer"
                    )
                }
                mEncoder.releaseOutputBuffer(encoderStatus, false)
                if (mBufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                    if (!endOfStream) {
                        Log.w(
                            TAG,
                            "reached end of stream unexpectedly"
                        )
                    } else {
                        if (VERBOSE) Log.d(
                            TAG,
                            "end of stream reached"
                        )
                    }
                    break // out of while
                }
            }
        }
    }

    private fun generateSurfaceFrame(frameIndex: Int) {
        val totalFrames: Int = (pagFile.duration() * pagFile.frameRate() / 1000000).toInt()
        val progress = frameIndex % totalFrames * 1.0f / totalFrames
        pagPlayer?.progress = progress.toDouble()
        pagPlayer?.flush()
    }
}