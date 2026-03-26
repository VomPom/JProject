package com.vompom.media.utils

import android.content.Context
import android.util.Log
import wang.julis.jwbase.basecompact.NaApplication
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 *
 * Created by @juliswang on 2025/09/16 20:01
 *
 * @Description
 */

object ResUtils {

    private const val TAG = "Resources"
    private const val ASSETS_MEDIA_PATH = "media"
    private const val SANDBOX_MEDIA_DIR = "media_files"

    // 缓存已复制的文件路径
    private val copiedFilesCache = mutableMapOf<String, String>()
    private var isInitialized = false


    /**
     * 初始化资源文件，将 assets/media 目录下的所有文件复制到应用沙盒目录
     * @param context 应用上下文
     * @return 是否初始化成功
     */
    fun init(context: Context): Boolean {
        if (isInitialized) {
            Log.d(TAG, "Media resources already initialized")
            return true
        }

        try {
            val assetsManager = context.assets
            val mediaFiles = assetsManager.list(ASSETS_MEDIA_PATH) ?: emptyArray()

            if (mediaFiles.isEmpty()) {
                Log.w(TAG, "No media files found in assets/$ASSETS_MEDIA_PATH")
                return false
            }

            // 创建沙盒目录
            val sandboxDir = File(context.filesDir, SANDBOX_MEDIA_DIR)
            if (!sandboxDir.exists()) {
                sandboxDir.mkdirs()
            }

            // 复制每个文件
            for (fileName in mediaFiles) {
                val success = copyAssetFileToSandbox(context, fileName, sandboxDir)
                if (success) {
                    val filePath = File(sandboxDir, fileName).absolutePath
                    copiedFilesCache[fileName] = filePath
                    Log.d(TAG, "Successfully copied $fileName to $filePath")
                } else {
                    Log.e(TAG, "Failed to copy $fileName")
                    return false
                }
            }

            isInitialized = true
            Log.i(TAG, "Media resources initialization completed. Copied ${mediaFiles.size} files.")
            return true

        } catch (e: Exception) {
            Log.e(TAG, "Error initializing media resources", e)
            return false
        }
    }

    /**
     * 复制单个 asset 文件到沙盒目录
     * @param context 应用上下文
     * @param fileName 文件名
     * @param targetDir 目标目录
     * @return 是否复制成功
     */
    private fun copyAssetFileToSandbox(context: Context, fileName: String, targetDir: File): Boolean {
        var inputStream: InputStream? = null
        var outputStream: FileOutputStream? = null

        try {
            val assetPath = "$ASSETS_MEDIA_PATH/$fileName"
            inputStream = context.assets.open(assetPath)

            val targetFile = File(targetDir, fileName)
            outputStream = FileOutputStream(targetFile)

            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
            }

            outputStream.flush()
            return true

        } catch (e: IOException) {
            Log.e(TAG, "Error copying asset file $fileName", e)
            return false
        } finally {
            try {
                inputStream?.close()
                outputStream?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error closing streams", e)
            }
        }
    }

    fun createSavePath(fileName: String): String {
        val context: Context = NaApplication.getApp()
        return context.getExternalFilesDir(null)!!.path.plus(fileName)
    }

    val testHok by path("hok.mp4")
    val testHokV by path("hok_v.mp4")
    val testWz by path("wz.mp4")
    val video30s by path("30s.mp4")
    val video10s by path("10s.mp4")
    val h264 by path("h264.h264")
    val pcm16k16bit by path("16k16bit.pcm")
    val pcm44_1k32bit by path("44_1k32bit.pcm")

    /**
     * 通过文件名获取沙盒目录中的文件绝对路径
     * @param fileName 文件名
     * @return 文件的绝对路径，如果文件不存在则返回 null
     */
    private fun path(fileName: String): Lazy<String> = lazy { copiedFilesCache[fileName]!! }

}