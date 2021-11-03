package com.tencent.tavcut.thumbnail

/**
 *
 * @author: mathewchen
 * e-mail : mathewchen@tencent.com
 * time   : 2021/05/30
 * desc   : 缩略图一些数据类
 * version: 1.0
 *
 */

data class ThumbAssetModel(val assetPath: String, val type: Int, val sourceTimeDurationUs: Long)

const val TYPE_VIDEO = 0
const val TYPE_IMAGE = 1