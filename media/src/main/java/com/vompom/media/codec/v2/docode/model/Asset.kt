package com.vompom.media.codec.v2.docode.model

import android.media.MediaFormat
import com.vompom.media.codec.v2.extractor.AssetExtractor

/**
 *
 * Created by @juliswang on 2025/10/20 18:40
 *
 * @Description
 */

class Asset {
    var path: String = ""
    var durationUs = 0L
    val extractor: AssetExtractor = AssetExtractor()

    constructor(path: String) {
        this.path = path
        extractor.setDataSource(path)
        initInfo()
    }

    private fun initInfo() {
        durationUs = getDuration(extractor.getMediaFormats())
    }

    fun getDuration(formats: List<MediaFormat>): Long {
        try {
            var videoDuration: Long = 0
            var audioDuration: Long = 0
            for (format in formats) {
                val mimeType = format.getString(MediaFormat.KEY_MIME)
                if (mimeType?.startsWith("video/") == true) {
                    if (format.containsKey(MediaFormat.KEY_DURATION)) {
                        videoDuration = format.getLong(MediaFormat.KEY_DURATION)
                    }
                } else if (mimeType?.startsWith("audio/") == true) {
                    if (format.containsKey(MediaFormat.KEY_DURATION)) {
                        audioDuration = format.getLong(MediaFormat.KEY_DURATION)
                    }
                }
            }
            return if (videoDuration > 0) videoDuration else audioDuration
        } catch (ignore: Exception) {
        } catch (ignore: Error) {
        }
        return 0
    }
}