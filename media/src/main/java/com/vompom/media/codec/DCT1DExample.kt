package com.vompom.media.codec

import kotlin.math.PI
import kotlin.math.sin

/**
 *
 * Created by @juliswang on 2025/11/03
 *
 * @Description 一维DCT使用示例
 */
object DCT1DExample {

    /**
     * 示例1：基本的DCT和IDCT变换
     */
    fun basicDCTExample() {
        println("=== 示例1：基本DCT/IDCT变换 ===")

        // 创建一个简单的信号
        val signal = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0)
        println("原始信号: ${signal.contentToString()}")

        // 进行DCT变换
        val dctCoefficients = DCT1D.dct(signal)
        println("DCT系数: ${dctCoefficients.contentToString()}")

        // 进行IDCT逆变换
        val reconstructed = DCT1D.idct(dctCoefficients)
        println("重建信号: ${reconstructed.contentToString()}")

        // 计算误差
        val error = signal.indices.sumOf {
            kotlin.math.abs(signal[it] - reconstructed[it])
        } / signal.size
        println("平均误差: $error\n")
    }

    /**
     * 示例2：能量守恒验证（帕塞瓦尔定理）
     */
    fun energyConservationExample() {
        println("=== 示例2：能量守恒验证 ===")

        val signal = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0)
        val dctCoefficients = DCT1D.dct(signal)

        val originalEnergy = DCT1D.calculateEnergy(signal)
        val dctEnergy = DCT1D.calculateEnergy(dctCoefficients)

        println("原始信号能量: $originalEnergy")
        println("DCT系数能量: $dctEnergy")
        println("能量保持: ${kotlin.math.abs(originalEnergy - dctEnergy) < 0.0001}\n")
    }

    /**
     * 示例3：信号压缩（低通滤波）
     */
    fun compressionExample() {
        println("=== 示例3：信号压缩 ===")

        // 创建一个包含高频和低频成分的信号
        val signal = DoubleArray(16) { i ->
            sin(2.0 * PI * i / 16) + 0.5 * sin(8.0 * PI * i / 16)
        }
        println("原始信号: ${signal.map { "%.2f".format(it) }}")

        // DCT变换
        val dctCoefficients = DCT1D.dct(signal)
        println("DCT系数: ${dctCoefficients.map { "%.2f".format(it) }}")

        // 保留50%的低频系数（压缩）
        val compressed = DCT1D.lowPassFilter(dctCoefficients, 0.5)
        println("压缩后系数(50%%): ${compressed.map { "%.2f".format(it) }}")

        // 重建信号
        val reconstructed = DCT1D.idct(compressed)
        println("重建信号: ${reconstructed.map { "%.2f".format(it) }}")

        // 计算压缩率
        val nonZeroCount = compressed.count { it != 0.0 }
        val compressionRatio = 100.0 * (1.0 - nonZeroCount.toDouble() / compressed.size)
        println("压缩率: %.1f%%\n".format(compressionRatio))
    }

    /**
     * 示例4：量化（类似JPEG压缩）
     */
    fun quantizationExample() {
        println("=== 示例4：量化压缩 ===")

        val signal = DoubleArray(8) { i ->
            100.0 + 50.0 * sin(2.0 * PI * i / 8)
        }
        println("原始信号: ${signal.map { "%.1f".format(it) }}")

        // DCT变换
        val dctCoefficients = DCT1D.dct(signal)
        println("DCT系数: ${dctCoefficients.map { "%.2f".format(it) }}")

        // 量化（量化步长=10）
        val quantized = DCT1D.quantize(dctCoefficients, 10.0)
        println("量化后: ${quantized.map { "%.2f".format(it) }}")

        // 重建信号
        val reconstructed = DCT1D.idct(quantized)
        println("重建信号: ${reconstructed.map { "%.1f".format(it) }}")

        // 计算损失
        val mse = signal.indices.sumOf {
            val diff = signal[it] - reconstructed[it]
            diff * diff
        } / signal.size
        println("均方误差(MSE): %.2f\n".format(mse))
    }

    /**
     * 示例5：分块DCT处理长信号
     */
    fun blockDCTExample() {
        println("=== 示例5：分块DCT处理 ===")

        // 创建一个长信号（20个样本）
        val signal = DoubleArray(20) { i ->
            sin(2.0 * PI * i / 20)
        }
        println("原始信号长度: ${signal.size}")
        println("原始信号: ${signal.map { "%.2f".format(it) }}")

        // 使用8样本块进行DCT
        val dctCoefficients = DCT1D.blockDCT(signal, blockSize = 8)
        println("DCT系数长度: ${dctCoefficients.size}")

        // 压缩：保留每个块的前4个系数
        val compressed = dctCoefficients.indices.map { i ->
            if (i % 8 < 4) dctCoefficients[i] else 0.0
        }.toDoubleArray()

        // 重建信号
        val reconstructed = DCT1D.blockIDCT(compressed, blockSize = 8, originalSize = signal.size)
        println("重建信号长度: ${reconstructed.size}")
        println("重建信号: ${reconstructed.map { "%.2f".format(it) }}")

        // 计算误差
        val error = signal.indices.sumOf {
            kotlin.math.abs(signal[it] - reconstructed[it])
        } / signal.size
        println("平均误差: %.4f\n".format(error))
    }

    /**
     * 示例6：不同类型的DCT比较
     */
    fun dctTypesExample() {
        println("=== 示例6：不同DCT类型比较 ===")

        val signal = doubleArrayOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0)
        println("原始信号: ${signal.contentToString()}")

        // DCT-II (最常用)
        val dct2 = DCT1D.dct(signal)
        println("DCT-II: ${dct2.map { "%.2f".format(it) }}")

        // DCT-I
        val dct1 = DCT1D.dctI(signal)
        println("DCT-I:  ${dct1.map { "%.2f".format(it) }}")

        // DCT-IV
        val dct4 = DCT1D.dctIV(signal)
        println("DCT-IV: ${dct4.map { "%.2f".format(it) }}")

        // 验证逆变换
        val reconstructed2 = DCT1D.idct(dct2)
        val reconstructed1 = DCT1D.idctI(dct1)
        val reconstructed4 = DCT1D.idctIV(dct4)

        println("\n逆变换验证:")
        println("IDCT-II: ${reconstructed2.map { "%.2f".format(it) }}")
        println("IDCT-I:  ${reconstructed1.map { "%.2f".format(it) }}")
        println("IDCT-IV: ${reconstructed4.map { "%.2f".format(it) }}\n")
    }

    /**
     * 示例7：高通滤波
     */
    fun highPassFilterExample() {
        println("=== 示例7：高通滤波 ===")

        // 创建一个低频信号加高频噪声
        val signal = DoubleArray(16) { i ->
            10.0 + sin(2.0 * PI * i / 16) + 0.3 * sin(10.0 * PI * i / 16)
        }
        println("原始信号(含噪声): ${signal.map { "%.2f".format(it) }}")

        // DCT变换
        val dctCoefficients = DCT1D.dct(signal)

        // 移除前20%的低频分量
        val filtered = DCT1D.highPassFilter(dctCoefficients, 0.2)

        // 重建信号（只保留高频成分）
        val reconstructed = DCT1D.idct(filtered)
        println("高通滤波后: ${reconstructed.map { "%.2f".format(it) }}\n")
    }

    /**
     * 运行所有示例
     */
    fun runAllExamples() {
        basicDCTExample()
        energyConservationExample()
        compressionExample()
        quantizationExample()
        blockDCTExample()
        dctTypesExample()
        highPassFilterExample()
    }
}

// 如果直接运行此文件，执行所有示例
fun main() {
    DCT1DExample.runAllExamples()
}
