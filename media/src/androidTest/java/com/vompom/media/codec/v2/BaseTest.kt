package com.vompom.media.codec.v2

import androidx.test.platform.app.InstrumentationRegistry
import com.vompom.media.utils.ResUtils
import org.junit.Before

/**
 *
 * Created by @juliswang on 2025/09/24 20:24
 *
 * @Description
 */

abstract class BaseTest {
    lateinit var mp4Path: String

    @Before
    fun setUp() {
        // 获取应用上下文并初始化资源文件
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val initSuccess = ResUtils.init(context)
        mp4Path = ResUtils.video10s
        assert(initSuccess) { "Failed to initialize media resources" }
    }
}