package com.tencent.tavcut.thumbnail

/**
 * @author: mathewchen
 * e-mail : mathewchen@tencent.com
 * time   : 2021/03/05
 * desc   :
 * version: 1.0
 */
internal interface IRunnableHandler {
    /**
     * postRunnable
     * @param runnable runnable
     */
    fun postRunnable(runnable: Runnable?)

    /**
     * runnable失败并释放部分内存
     */
    fun runnableFailAndReleaseCache()
}