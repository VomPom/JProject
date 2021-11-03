package com.tencent.tavcut.thumbnail

import android.graphics.Bitmap

/**
 * @author: mathewchen
 * e-mail : mathewchen@tencent.com
 * time   : 2021/03/05
 * desc   : 负责监听缩略图生成
 * version: 2.0
 */
fun interface IThumbnailGeneratedListener {
    /**
     * 封面生成
     * @param tag  请求时所带信息
     * @param startTimeMs 生成时间
     * @param bitmap 该视频片段对应的封面
     */
    fun onThumbnailGenerated(
        tag: Any?,
        startTimeMs: Long,
        bitmap: Bitmap?
    )
}