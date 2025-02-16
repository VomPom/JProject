package wang.julis.jproject.example.little

import wang.julis.jwbase.utils.Logger

/**
 * Created by @juliswang on 2025/02/14 16:50
 *
 * @Description
 *    位标志设计的好处
 *      - 节省内存
 *          传统方式:如果每个状态都用一个布尔变量表示，假设有 32 个状态，则需要 32 个 `boolean` 变量，占用 32 字节（每个 `boolean` 在 Java 中占用 1 字节）
 *          位标志方式:使用一个 `int`（4 字节）即可表示 32 个状态，内存占用减少为原来的 1/8。
 *      - 高效性能
 *          使用按位或运算（`|`）快速设置标志位，使用按位与运算（`&`）和取反运算（`~`）快速清除标志位。使用按位与运算（`&`）快速检查标志位。
 *          位运算是 CPU 的基本操作,相比于传统的 `if-else` 或 `switch-case` 逻辑，位运算的性能更高
 *      - 代码简洁
 *          通过位运算，可以用一行代码完成多个状态的设置、清除或检查，代码更加简洁易读。
 *      - 可扩展性
 *          如果需要新增状态，只需定义一个新的标志位，而不需要修改现有的数据结构
 *
 */
object BitMarkTest {

    /**
     * 以一个实际的业务场景举例:
     *          先是网络请求，网络请求之后基于成功的数据进行处理，成功处理则返回数据，失败的话，则返回错误码
     *
     *    失败场景可能有：网络失败、业务使用失败(可能数据中途有很多层逻辑，这里统一为一层)
     *    网络请求可能会存在以下场景：
     *          A、URL 错误
     *          B、超时
     *          C、数据解析失败
     *          D、服务器错误
     *          ……
     *    业务使用失败
     *          a、case 1 失败
     *          b、case 2 失败
     *          c、case 3 失败
     *          ……
     *
     */

    // 定义错误状态
    private const val FLAG_URL_ERROR = 0x00000001
    private const val FLAG_TIMEOUT = 0x00000002
    private const val FLAG_PARSE_ERROR = 0x00000004
    private const val FLAG_SERVER_ERROR = 0x00000008
    private const val FLAG_CASE_1 = 0x00000010
    private const val FLAG_CASE_2 = 0x00000020
    private const val FLAG_CASE_3 = 0x00000040

    /*
     * Masks for status
     *
     * |-------|-------|
     *                1 FLAG_URL_ERROR
     *               1  FLAG_TIMEOUT
     *              1   FLAG_PARSE_ERROR
     *             1    FLAG_SERVER_ERROR
     *             11   FLAG_ERROR_MASK  // 包含 FLAG_SERVER_ERROR 和 FLAG_PARSE_ERROR 错误
     *            1     FLAG_CASE_1
     *           1      FLAG_CASE_2
     *          1       FLAG_CASE_3
     *         1        FLAG_CASE_4
     *         11       FLAG_CASE_MASK   // 包含 CASE_4 和 CASE_3 的场景
     *                   ......
     */

    fun run() {
        var check: Boolean
        var status: Int = FLAG_URL_ERROR

        // 网络请求A错误和业务使用 结合
        status = status or FLAG_CASE_1                        // 等价于 Java status |= FLAG_CASE_1
        Logger.d(status.toString(2))                    // 打印  10001

        // 检查是否有 FLAG_CASE_1  状态
        check = (status.and(FLAG_CASE_1)) != 0                // true
        Logger.d("$check-> (status and FLAG_CASE_1) != 0")
        check = (status.and(FLAG_CASE_2)) != 0                // false
        Logger.d("$check-> (status and FLAG_CASE_2) != 0")

        // 清除 FLAG_URL_ERROR 的标记
        status = status and FLAG_URL_ERROR.inv()              // 等价于 Java status &= ~FLAG_URL_ERROR;
        check = (status.and(FLAG_URL_ERROR)) != 0             // false
        Logger.d("$check-> (status.and(FLAG_URL_ERROR)) != 0")
        Logger.d(status.toString(2))                     // 打印 10000
    }
}


