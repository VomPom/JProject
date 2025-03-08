package wang.julis.jproject.example.little

import android.content.Context
import android.graphics.Matrix
import wang.julis.jproject.example.IBaseTest
import wang.julis.jwbase.utils.Logger

/**
 *
 * Created by @juliswang on 2025/02/18 10:04
 *
 * @Description
 */
object MatrixTest : IBaseTest() {
    override fun run(context: Context?) {
        var matrix = Matrix()
        matrix.postRotate(90f)
        print("rotate 90", matrix)

        matrix = Matrix()
        matrix.postRotate(45f)
        print("rotate 45", matrix)

        matrix = Matrix()
        matrix.postRotate(90f, 100f, 100f)
        print("rotate (px 100,py 100)", matrix)


        matrix = Matrix()
        matrix.preTranslate(100f, 100f)
        print("translate (dx 100, dy 100)", matrix)
    }

    private fun print(msg: String, matrix: Matrix) {
        val values = FloatArray(9)
        matrix.getValues(values) // 获取矩阵的 9 个值
        var result = ""
        for (i in 0 until 3) {
            val row = values.sliceArray(i * 3 until (i * 3) + 3)
            val formattedRow = row.joinToString(" | ") { "%-4s".format(it) }
            result += "\n[$formattedRow]"
        }
        Logger.d("$msg: $result")
    }
}