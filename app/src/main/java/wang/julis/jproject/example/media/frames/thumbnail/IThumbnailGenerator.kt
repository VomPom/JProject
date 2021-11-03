package com.tencent.tavcut.thumbnail

/**
 *
 * @author: mathewchen
 * e-mail : mathewchen@tencent.com
 * time   : 2021/03/17
 * desc   : 生成视频截图的接口
 * version: 1.0
 *
 */
internal interface IThumbnailGenerator {
    /**
     * 生成指定位置的封面（用于剪辑页替换视频的场景） 若指定位置的封面任务还没开始执行，则直接更新对应封面生成时间点 若指定位置的封面任务正在执行或者已经执行完，则添加一个新的封面任务在队列尾部
     *
     * @param timeUs 封面生成的时间点
     */
    fun generateThumbnailByTime(timeUs: Long)

    /**
     * 暂停截图任务，释放截图器资源，关闭截图器任务线程池
     */
    fun pause()

    /**
     * 继续截图器任务，从中断的地方开始截图
     */
    fun resume()

    /**
     * 销毁资源
     */
    fun release()
}