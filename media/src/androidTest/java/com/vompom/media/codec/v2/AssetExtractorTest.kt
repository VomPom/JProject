package com.vompom.media.codec.v2

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vompom.media.codec.v2.extractor.AssetExtractor
import com.vompom.media.utils.ResUtils
import org.junit.Test
import org.junit.runner.RunWith
import wang.julis.jwbase.utils.Logger
import java.nio.ByteBuffer

/**
 * Android测试类，用于测试AssetExtractor功能
 *
 * Created by @juliswang on 2025/09/24 17:14
 *
 * @Description 在Android环境中测试AssetExtractor，需要处理本地文件路径
 */
@RunWith(AndroidJUnit4::class)
class AssetExtractorTest : BaseTest() {

    @Test
    fun testAssetExtractor() {
        val extractor = AssetExtractor()
        extractor.setDataSource(ResUtils.video10s)
        extractor.selectTrack(0)
        val buffer = ByteBuffer.allocate(500 * 1024)

        var totalFrames = 0
        while (true) {
            val size = extractor.readSampleData(buffer)
            Logger.d("AssetExtractorTest", "got buffer size: $size")
            if (size <= 0) {
                Logger.d("AssetExtractorTest", "no more buffer: $size, total frames: $totalFrames")
                break
            }
            totalFrames++

            // 防止无限循环，设置最大帧数限制
            if (totalFrames > 1000) {
                Logger.w("AssetExtractorTest", "Reached maximum frame limit, stopping test")
                break
            }
        }

        // 验证至少读取到了一些数据
        assert(totalFrames > 0) { "No frames were extracted from the video" }
    }
}