package com.vompom.media.codec

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 *
 * Created by @juliswang on 2025/11/03 21:32
 *
 * @Description 离散余弦变换（DCT）和逆变换（IDCT）实现
 */

class DCT {

    companion object {
        private const val BLOCK_SIZE = 8

        /**
         * 对输入图片进行离散余弦变换
         * @param inputBitmap 输入的原始图片
         * @return 变换后的图片数据（DCT系数可视化）
         */
        fun applyDCT(inputBitmap: Bitmap): Bitmap {
            val width = inputBitmap.width
            val height = inputBitmap.height

            // 创建输出bitmap
            val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            // 提取每个颜色通道
            val redChannel = Array(height) { IntArray(width) }
            val greenChannel = Array(height) { IntArray(width) }
            val blueChannel = Array(height) { IntArray(width) }

            // 读取像素数据
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val pixel = inputBitmap.getPixel(x, y)
                    redChannel[y][x] = Color.red(pixel)
                    greenChannel[y][x] = Color.green(pixel)
                    blueChannel[y][x] = Color.blue(pixel)
                }
            }

            // 对每个通道进行DCT变换
            val dctRed = processDCT(redChannel, width, height)
            val dctGreen = processDCT(greenChannel, width, height)
            val dctBlue = processDCT(blueChannel, width, height)

            // 将DCT系数转换为可视化图像（归一化到0-255范围）
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val r = normalizeCoefficient(dctRed[y][x])
                    val g = normalizeCoefficient(dctGreen[y][x])
                    val b = normalizeCoefficient(dctBlue[y][x])
                    outputBitmap.setPixel(x, y, Color.rgb(r, g, b))
                }
            }

            return outputBitmap
        }

        /**
         * 对DCT变换后的数据进行逆变换
         * @param dctBitmap DCT变换后的图片数据
         * @return 逆变换还原后的图片
         */
        fun applyIDCT(dctBitmap: Bitmap): Bitmap {
            val width = dctBitmap.width
            val height = dctBitmap.height

            // 创建输出bitmap
            val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            // 提取DCT系数（反归一化）
            val dctRed = Array(height) { DoubleArray(width) }
            val dctGreen = Array(height) { DoubleArray(width) }
            val dctBlue = Array(height) { DoubleArray(width) }

            for (y in 0 until height) {
                for (x in 0 until width) {
                    val pixel = dctBitmap.getPixel(x, y)
                    dctRed[y][x] = denormalizeCoefficient(Color.red(pixel))
                    dctGreen[y][x] = denormalizeCoefficient(Color.green(pixel))
                    dctBlue[y][x] = denormalizeCoefficient(Color.blue(pixel))
                }
            }

            // 对每个通道进行IDCT逆变换
            val redChannel = processIDCT(dctRed, width, height)
            val greenChannel = processIDCT(dctGreen, width, height)
            val blueChannel = processIDCT(dctBlue, width, height)

            // 合并通道并写入输出bitmap
            for (y in 0 until height) {
                for (x in 0 until width) {
                    val r = clamp(redChannel[y][x].roundToInt())
                    val g = clamp(greenChannel[y][x].roundToInt())
                    val b = clamp(blueChannel[y][x].roundToInt())
                    outputBitmap.setPixel(x, y, Color.rgb(r, g, b))
                }
            }

            return outputBitmap
        }

        /**
         * 对整个图像进行DCT处理（以8x8块为单位）
         */
        private fun processDCT(channel: Array<IntArray>, width: Int, height: Int): Array<DoubleArray> {
            val result = Array(height) { DoubleArray(width) }

            // 以8x8块为单位进行DCT
            for (blockY in 0 until height step BLOCK_SIZE) {
                for (blockX in 0 until width step BLOCK_SIZE) {
                    val block = extractBlock(channel, blockX, blockY, width, height)
                    val dctBlock = dct2D(block)
                    insertBlock(result, dctBlock, blockX, blockY, width, height)
                }
            }

            return result
        }

        /**
         * 对整个图像进行IDCT处理（以8x8块为单位）
         */
        private fun processIDCT(dctData: Array<DoubleArray>, width: Int, height: Int): Array<DoubleArray> {
            val result = Array(height) { DoubleArray(width) }

            // 以8x8块为单位进行IDCT
            for (blockY in 0 until height step BLOCK_SIZE) {
                for (blockX in 0 until width step BLOCK_SIZE) {
                    val block = extractBlockDouble(dctData, blockX, blockY, width, height)
                    val idctBlock = idct2D(block)
                    insertBlock(result, idctBlock, blockX, blockY, width, height)
                }
            }

            return result
        }

        /**
         * 2D DCT变换（8x8块）
         */
        private fun dct2D(block: Array<DoubleArray>): Array<DoubleArray> {
            val N = BLOCK_SIZE
            val result = Array(N) { DoubleArray(N) }

            for (u in 0 until N) {
                for (v in 0 until N) {
                    var sum = 0.0

                    for (x in 0 until N) {
                        for (y in 0 until N) {
                            val pixel = block[y][x] - 128.0 // 中心化到[-128, 127]
                            sum += pixel *
                                    cos((2.0 * x + 1.0) * u * PI / (2.0 * N)) *
                                    cos((2.0 * y + 1.0) * v * PI / (2.0 * N))
                        }
                    }

                    val cu = if (u == 0) 1.0 / sqrt(2.0) else 1.0
                    val cv = if (v == 0) 1.0 / sqrt(2.0) else 1.0
                    result[v][u] = 0.25 * cu * cv * sum
                }
            }

            return result
        }

        /**
         * 2D IDCT逆变换（8x8块）
         */
        private fun idct2D(dctBlock: Array<DoubleArray>): Array<DoubleArray> {
            val N = BLOCK_SIZE
            val result = Array(N) { DoubleArray(N) }

            for (x in 0 until N) {
                for (y in 0 until N) {
                    var sum = 0.0

                    for (u in 0 until N) {
                        for (v in 0 until N) {
                            val cu = if (u == 0) 1.0 / sqrt(2.0) else 1.0
                            val cv = if (v == 0) 1.0 / sqrt(2.0) else 1.0

                            sum += cu * cv * dctBlock[v][u] *
                                    cos((2.0 * x + 1.0) * u * PI / (2.0 * N)) *
                                    cos((2.0 * y + 1.0) * v * PI / (2.0 * N))
                        }
                    }

                    result[y][x] = 0.25 * sum + 128.0 // 反中心化
                }
            }

            return result
        }

        /**
         * 从图像中提取8x8块（整数）
         */
        private fun extractBlock(
            data: Array<IntArray>,
            startX: Int,
            startY: Int,
            width: Int,
            height: Int
        ): Array<DoubleArray> {
            val block = Array(BLOCK_SIZE) { DoubleArray(BLOCK_SIZE) }

            for (y in 0 until BLOCK_SIZE) {
                for (x in 0 until BLOCK_SIZE) {
                    val pixelX = (startX + x).coerceIn(0, width - 1)
                    val pixelY = (startY + y).coerceIn(0, height - 1)
                    block[y][x] = data[pixelY][pixelX].toDouble()
                }
            }

            return block
        }

        /**
         * 从图像中提取8x8块（浮点）
         */
        private fun extractBlockDouble(
            data: Array<DoubleArray>,
            startX: Int,
            startY: Int,
            width: Int,
            height: Int
        ): Array<DoubleArray> {
            val block = Array(BLOCK_SIZE) { DoubleArray(BLOCK_SIZE) }

            for (y in 0 until BLOCK_SIZE) {
                for (x in 0 until BLOCK_SIZE) {
                    val pixelX = (startX + x).coerceIn(0, width - 1)
                    val pixelY = (startY + y).coerceIn(0, height - 1)
                    block[y][x] = data[pixelY][pixelX]
                }
            }

            return block
        }

        /**
         * 将8x8块插入到结果图像中
         */
        private fun insertBlock(
            result: Array<DoubleArray>,
            block: Array<DoubleArray>,
            startX: Int,
            startY: Int,
            width: Int,
            height: Int
        ) {
            for (y in 0 until BLOCK_SIZE) {
                for (x in 0 until BLOCK_SIZE) {
                    val pixelX = startX + x
                    val pixelY = startY + y
                    if (pixelX < width && pixelY < height) {
                        result[pixelY][pixelX] = block[y][x]
                    }
                }
            }
        }

        /**
         * 归一化DCT系数到0-255范围（用于可视化）
         */
        private fun normalizeCoefficient(value: Double): Int {
            // DCT系数范围大约在[-1024, 1024]，缩放到[0, 255]
            val normalized = ((value + 1024.0) / 2048.0 * 255.0).roundToInt()
            return clamp(normalized)
        }

        /**
         * 反归一化：从0-255恢复DCT系数
         */
        private fun denormalizeCoefficient(value: Int): Double {
            return (value / 255.0 * 2048.0) - 1024.0
        }

        /**
         * 限制值在0-255范围内
         */
        private fun clamp(value: Int): Int {
            return value.coerceIn(0, 255)
        }
    }
}