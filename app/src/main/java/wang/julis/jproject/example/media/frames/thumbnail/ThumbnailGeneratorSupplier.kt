package com.tencent.tavcut.thumbnail


/**
 * @author: mathewchen
 * e-mail : mathewchen@tencent.com
 * time   : 2021/03/05
 * desc   : 为截图生成器提供数据及Handler
 * version: 1.0
 */
internal class ThumbnailGeneratorSupplier constructor(
    /**
     * 缩略图生成监听
     */
    val generatedListener: IThumbnailGeneratedListener,

    /**
     * 生成缩略图的宽度
     */
    val thumbnailWidth: Int = 0,

    /**
     * 生成缩略图的高度
     */
    val thumbnailHeight: Int = 0,

    /**
     * 任务处理器
     */
    val generateHandler: IRunnableHandler
)