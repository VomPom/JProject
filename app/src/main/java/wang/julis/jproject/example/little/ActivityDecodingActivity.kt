package wang.julis.jproject.example.little

import android.annotation.SuppressLint
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import com.julis.wang.R
import wang.julis.jwbase.basecompact.BaseActivity
import java.nio.charset.Charset

/**

 * Created by @juliswang on 2024/04/11 19:29
 *
 * @Description
 */
class ActivityDecodingActivity : BaseActivity() {
    private val TAG = "--julis"
    private var tvDecoding: TextView? = null

    @SuppressLint("SetTextI18n")
    override fun initView() {
        tvDecoding = findViewById(R.id.tv_result)
        val ediTextView = findViewById<EditText>(R.id.et_text)

        val iniText = "中文"
        ediTextView.setText(iniText)
        tvDecoding?.text = format(iniText)
        ediTextView.doOnTextChanged { text, _, _, _ ->
            tvDecoding?.text = format(text)
        }
    }

    /**
    ASCII 码 ( American Standard Code for Information Interchange)
    256个符号，从 00000000 到 11111111

    ANSI（American National Standards Institute),ANSI编码指：
    美国和西欧：Windows-1252
    中文（简体）：GB2312 或 GBK
    中文（繁体）：Big5
    日文：Shift-JIS
    韩文：EUC-KR


    UTF-8 是 Unicode 的实现方式之一
    Unicode 符号范围        |   UTF-8编码方式
    (十六进制)             |  （二进制）
    ----------------------+----------------------------------
    0000 0000 ~ 0000 007F | 0xxxxxxx
    0000 0080 ~ 0000 07FF | 110xxxxx 10xxxxxx
    0000 0800 ~ 0000 FFFF | 1110xxxx 10xxxxxx 10xxxxxx
    0001 0000 ~ 0010 FFFF | 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx

    Unicode
    "FE FF" 是 Unicode 字符串的字节顺序标记（Byte Order Mark，简称 BOM），用于表示字符串的字节顺序
    Unicode Little-Endian，"FF FE"
    Unicode Big-Endian，"FE FF"
     *
     * @param text
     * @return
     */
    private fun format(text: CharSequence?): String {
        return """
                "text   ": $text
                "UTF_8  ": ${text?.toCharset(Charsets.UTF_8)}
                "GBK    ": ${text?.toCharset(Charset.forName("GBK"))}
                "Unicode": ${text?.toCharset(Charsets.UTF_16)}
            """.trimIndent()
    }

    override fun initData() {
        Log.d(TAG, format("a"))       // utf8 一字节
        Log.d(TAG, format("ε"))       // utf8 二字节 带有附加符号的拉丁文、希腊文、西里尔字母、亚美尼亚语、希伯来文、阿拉伯文、叙利亚文及它拿字母则需要二个字节编码
        Log.d(TAG, format("啊"))      // utf8 三字节 基本等同于GBK，含21000多个汉字
        Log.d(TAG, format("👧"))     // utf8 四字节 中日韩超大字符集里面的汉字，有5万多个

        // 在 UTF-8 编码中，"EF BF BD" 是一个特殊的字符，表示 REPLACEMENT CHARACTER（替换字符）
        // 当解码器在解码字节序列时遇到无法识别的字节或无效的编码时，通常会用 REPLACEMENT CHARACTER（U+FFFD）替换这些无效的字节
        // "EF BF BD" 在 GBK 里面则编码成 "锟斤拷"
        Log.d(TAG, format("锟斤拷"))

        // 0xCC
        Log.d(TAG, format("烫"))

//        printUnicode()
//        printGBK()

    }

    override fun getContentView(): Int {
        return R.layout.activity_character_decoding
    }


    /**
     * 打印所有 Unicode 字符
     *
     */
    private fun printUnicode() {
        // Unicode 字符范围：U+0000到U+10FFFF
        val start = 0x0000
        val end = 0x10FFFF // ！！！！整体会卡死，调整 end 为较小的值进行打印！！！！
        val strBuilder: StringBuilder = StringBuilder()
        for (codePoint in start..end) {
            // Unicode中的U+D800到U+DFFF 区域被称为代理区（Surrogate Area），这个区域的字符不代表任何有效的Unicode字符
            if (codePoint in 0xD800..0xDFFF) {
                continue
            }
            // 将Unicode码点转换为UTF-8编码的字符串
            val utf8Char = String(Character.toChars(codePoint))

            strBuilder.append(utf8Char)
            if (codePoint % 100 == 0) {
                strBuilder.append("\n")
                Log.e(TAG, strBuilder.toString())
                strBuilder.clear()
            }
        }
    }

    /**
     * 打印所有GBK 字符集
     * GBK 字符集的编码范围是从 0x8140 开始的。在 0x8140 之前的编码范围主要包括 ASCII 字符（0x00 到 0x7F）和一些扩展的 ASCII 字符（0x80 到 0xA0）
     */
    private fun printGBK() {
        val gbk = Charset.forName("GBK")
        val strBuilder: StringBuilder = StringBuilder()
        for (codePoint in 0x8140..0xFEFE) {
            val bytes = byteArrayOf((codePoint shr 8).toByte(), (codePoint and 0xFF).toByte())
            val gbkChar = String(bytes, gbk)
            strBuilder.append(gbkChar).append(" ").append(codePoint)
            if (codePoint % 100 == 0) {
                strBuilder.append("\n")
                Log.e(TAG, strBuilder.toString())
                strBuilder.clear()
            }
        }
    }

    private fun CharSequence.toCharset(charset: Charset): String {
        val byte = this.toString().toByteArray(charset)
        return byte.joinToString(separator = " ") { String.format("%02X", it) }
    }

}
