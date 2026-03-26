package com.vompom.media.codec

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

/**
 *
 * Created by @juliswang on 2025/11/03
 *
 * @Description 一维离散余弦变换（1D DCT）和逆变换（IDCT）实现
 * 支持DCT-I, DCT-II, DCT-III, DCT-IV等多种类型
 */
class DCT1D {

    companion object {
        /**
         * DCT-II 类型（最常用的DCT类型）
         * 这是JPEG、MP3等标准使用的DCT类型
         * @param input 输入信号数组
         * @return DCT系数数组
         */
        fun dct(input: DoubleArray): DoubleArray {
            val N = input.size
            val output = DoubleArray(N)

            for (k in 0 until N) {
                var sum = 0.0
                for (n in 0 until N) {
                    sum += input[n] * cos(PI * k * (2.0 * n + 1.0) / (2.0 * N))
                }
                // 归一化系数
                val alpha = if (k == 0) sqrt(1.0 / N) else sqrt(2.0 / N)
                output[k] = alpha * sum
            }

            return output
        }

        /**
         * DCT-III 类型（DCT-II的逆变换，通常称为IDCT）
         * @param input DCT系数数组
         * @return 还原后的信号数组
         */
        fun idct(input: DoubleArray): DoubleArray {
            val N = input.size
            val output = DoubleArray(N)

            for (n in 0 until N) {
                var sum = 0.0
                for (k in 0 until N) {
                    val alpha = if (k == 0) sqrt(1.0 / N) else sqrt(2.0 / N)
                    sum += alpha * input[k] * cos(PI * k * (2.0 * n + 1.0) / (2.0 * N))
                }
                output[n] = sum
            }

            return output
        }

        /**
         * DCT-I 类型
         * @param input 输入信号数组
         * @return DCT系数数组
         */
        fun dctI(input: DoubleArray): DoubleArray {
            val N = input.size
            if (N < 2) throw IllegalArgumentException("DCT-I requires at least 2 points")

            val output = DoubleArray(N)

            for (k in 0 until N) {
                var sum = 0.0
                for (n in 0 until N) {
                    sum += input[n] * cos(PI * k * n / (N - 1.0))
                }

                // 归一化系数
                val alpha = when {
                    k == 0 || k == N - 1 -> sqrt(1.0 / (N - 1.0))
                    else -> sqrt(2.0 / (N - 1.0))
                }
                output[k] = alpha * sum
            }

            return output
        }

        /**
         * IDCT-I 类型（DCT-I的逆变换，DCT-I是对称的）
         * @param input DCT系数数组
         * @return 还原后的信号数组
         */
        fun idctI(input: DoubleArray): DoubleArray {
            // DCT-I是对称的，所以IDCT-I和DCT-I相同
            return dctI(input)
        }

        /**
         * DCT-IV 类型
         * @param input 输入信号数组
         * @return DCT系数数组
         */
        fun dctIV(input: DoubleArray): DoubleArray {
            val N = input.size
            val output = DoubleArray(N)

            for (k in 0 until N) {
                var sum = 0.0
                for (n in 0 until N) {
                    sum += input[n] * cos(PI * (k + 0.5) * (n + 0.5) / N)
                }
                output[k] = sqrt(2.0 / N) * sum
            }

            return output
        }

        /**
         * IDCT-IV 类型（DCT-IV的逆变换，DCT-IV也是对称的）
         * @param input DCT系数数组
         * @return 还原后的信号数组
         */
        fun idctIV(input: DoubleArray): DoubleArray {
            // DCT-IV是对称的，所以IDCT-IV和DCT-IV相同
            return dctIV(input)
        }

        /**
         * 分块DCT处理（将长信号分成多个块进行DCT）
         * @param input 输入信号数组
         * @param blockSize 块大小（默认8，类似JPEG）
         * @return DCT系数数组
         */
        fun blockDCT(input: DoubleArray, blockSize: Int = 8): DoubleArray {
            val numBlocks = (input.size + blockSize - 1) / blockSize
            val output = DoubleArray(numBlocks * blockSize)

            for (i in 0 until numBlocks) {
                val startIdx = i * blockSize
                val endIdx = minOf(startIdx + blockSize, input.size)

                // 提取块
                val block = DoubleArray(blockSize)
                for (j in 0 until blockSize) {
                    block[j] = if (startIdx + j < endIdx) input[startIdx + j] else 0.0
                }

                // 对块进行DCT
                val dctBlock = dct(block)

                // 写入结果
                for (j in 0 until blockSize) {
                    output[startIdx + j] = dctBlock[j]
                }
            }

            return output
        }

        /**
         * 分块IDCT处理（将DCT系数分成多个块进行IDCT）
         * @param input DCT系数数组
         * @param blockSize 块大小（默认8）
         * @param originalSize 原始信号长度（用于裁剪）
         * @return 还原后的信号数组
         */
        fun blockIDCT(input: DoubleArray, blockSize: Int = 8, originalSize: Int = input.size): DoubleArray {
            val numBlocks = (input.size + blockSize - 1) / blockSize
            val output = DoubleArray(numBlocks * blockSize)

            for (i in 0 until numBlocks) {
                val startIdx = i * blockSize

                // 提取块
                val block = DoubleArray(blockSize)
                for (j in 0 until blockSize) {
                    if (startIdx + j < input.size) {
                        block[j] = input[startIdx + j]
                    }
                }

                // 对块进行IDCT
                val idctBlock = idct(block)

                // 写入结果
                for (j in 0 until blockSize) {
                    if (startIdx + j < output.size) {
                        output[startIdx + j] = idctBlock[j]
                    }
                }
            }

            // 裁剪到原始大小
            return output.copyOf(originalSize)
        }

        /**
         * 计算信号的能量（用于验证帕塞瓦尔定理）
         * @param signal 信号数组
         * @return 信号能量
         */
        fun calculateEnergy(signal: DoubleArray): Double {
            return signal.sumOf { it * it }
        }

        /**
         * 归一化信号到[-1, 1]范围
         * @param signal 输入信号
         * @return 归一化后的信号
         */
        fun normalize(signal: DoubleArray): DoubleArray {
            val max = signal.maxOrNull()?.let { kotlin.math.abs(it) } ?: 1.0
            val min = signal.minOrNull()?.let { kotlin.math.abs(it) } ?: 1.0
            val maxAbs = maxOf(max, min)

            return if (maxAbs > 0) {
                signal.map { it / maxAbs }.toDoubleArray()
            } else {
                signal
            }
        }

        /**
         * 对DCT系数进行量化（类似JPEG压缩）
         * @param coefficients DCT系数
         * @param quantizationStep 量化步长
         * @return 量化后的系数
         */
        fun quantize(coefficients: DoubleArray, quantizationStep: Double): DoubleArray {
            return coefficients.map {
                (it / quantizationStep).toInt() * quantizationStep
            }.toDoubleArray()
        }

        /**
         * 低通滤波：保留低频系数，丢弃高频系数
         * @param coefficients DCT系数
         * @param keepRatio 保留系数的比例（0.0-1.0）
         * @return 滤波后的系数
         */
        fun lowPassFilter(coefficients: DoubleArray, keepRatio: Double): DoubleArray {
            val keepCount = (coefficients.size * keepRatio.coerceIn(0.0, 1.0)).toInt()
            return coefficients.mapIndexed { index, value ->
                if (index < keepCount) value else 0.0
            }.toDoubleArray()
        }

        /**
         * 高通滤波：保留高频系数，丢弃低频系数
         * @param coefficients DCT系数
         * @param removeRatio 丢弃系数的比例（0.0-1.0）
         * @return 滤波后的系数
         */
        fun highPassFilter(coefficients: DoubleArray, removeRatio: Double): DoubleArray {
            val removeCount = (coefficients.size * removeRatio.coerceIn(0.0, 1.0)).toInt()
            return coefficients.mapIndexed { index, value ->
                if (index < removeCount) 0.0 else value
            }.toDoubleArray()
        }
    }
}
