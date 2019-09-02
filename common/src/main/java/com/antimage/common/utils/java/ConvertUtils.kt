package com.antimage.common.utils.java

import android.annotation.SuppressLint
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.experimental.and

/**
 * Created by xuyuming on 2019-08-29
 */
object ConvertUtils {

    private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    /**
     * byteArr转hexString
     *
     * 例如：
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes 字节数组
     * @return 16进制大写字符串
     */
    fun bytes2HexString(bytes: ByteArray?): String {
        if (bytes == null) return ""
        val len = bytes.size
        if (len <= 0) return ""
        val ret = CharArray(len shl 1)
        var i = 0
        var j = 0
        while (i < len) {
            ret[j++] = hexDigits[bytes[i].toInt().ushr(4) and 0x0f]
            ret[j++] = hexDigits[bytes[i].toInt() and 0x0f]
            i++
        }
        return String(ret)
    }


    /**
     * hexString转byteArr
     *
     * 例如：
     * hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
     *
     * @param hexString 十六进制字符串
     * @return 字节数组
     */
    fun hexString2Bytes(hexString: String): ByteArray? {
        var hexString = hexString
        if (hexString.trim().isEmpty()) return null
        var len = hexString.length
        if (len % 2 != 0) {
            hexString = "0$hexString"
            len += 1
        }
        val hexBytes = hexString.toUpperCase().toCharArray()
        val ret = ByteArray(len shr 1)
        var i = 0
        while (i < len) {
            ret[i shr 1] = (hex2Dec(hexBytes[i]) shl 4 or hex2Dec(hexBytes[i + 1])).toByte()
            i += 2
        }
        return ret
    }


    /**
     * hexChar转int
     *
     * @param hexChar hex单个字节
     * @return 0..15
     */
    private fun hex2Dec(hexChar: Char): Int {
        return when (hexChar) {
            in '0'..'9' -> hexChar - '0'
            in 'a'..'f' -> hexChar - 'a' + 10
            else -> throw IllegalArgumentException()
        }
    }


    /**
     * 字节数转合适内存大小
     *
     * 保留3位小数
     *
     * @param byteNum 字节数
     * @return 合适内存大小
     */
    @SuppressLint("DefaultLocale")
    fun byte2FitMemorySize(byteNum: Long): String {
        return when {
            byteNum < 0 -> "shouldn't be less than zero!"
            byteNum < 1024 -> String.format("%.3fB", byteNum + 0.0005)
            byteNum < 1048576 -> String.format("%.3fKB", byteNum / 1024 + 0.0005)
            byteNum < 1073741824 -> String.format("%.3fMB", byteNum / 1048576 + 0.0005)
            else -> String.format("%.3fGB", byteNum / 1073741824 + 0.0005)
        }
    }


    /**
     * inputStream转byteArr
     *
     * @param is 输入流
     * @return 字节数组
     */
    fun inputStream2Bytes(ins: InputStream?): ByteArray? {
        return if (ins == null) null else input2OutputStream(ins)!!.toByteArray()
    }


    /**
     * inputStream转outputStream
     *
     * @param is 输入流
     * @return outputStream子类
     */
    fun input2OutputStream(ins: InputStream?): ByteArrayOutputStream? {
        if (ins == null) return null
        try {
            val os = ByteArrayOutputStream()
            val b = ByteArray(1024)
            var len: Int
            ins.use { input ->
                os.use { it ->
                    while (input.read(b).also { len = it } != -1) {
                        it.write(b, 0, len)
                    }
                }
            }
            return os
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            CloseUtils.closeIO(ins)
        }
    }


    /**
     * 以unit为单位的时间长度转毫秒时间戳
     *
     * @param timeSpan 毫秒时间戳
     * @param unit     单位类型
     *
     *  * [TimeUtils.TimeUnit.MSEC]: 毫秒
     *  * [TimeUtils.TimeUnit.SEC]: 秒
     *  * [TimeUtils.TimeUnit.MIN]: 分
     *  * [TimeUtils.TimeUnit.HOUR]: 小时
     *  * [TimeUtils.TimeUnit.DAY]: 天
     *
     * @return 毫秒时间戳
     */
    fun timeSpan2Millis(timeSpan: Long, unit: TimeUtils.TimeUnit): Long {
        return when (unit) {
            TimeUtils.TimeUnit.MSEC -> timeSpan
            TimeUtils.TimeUnit.SEC -> timeSpan * TimeUtils.SEC
            TimeUtils.TimeUnit.MIN -> timeSpan * TimeUtils.MIN
            TimeUtils.TimeUnit. HOUR -> timeSpan * TimeUtils.HOUR
            TimeUtils.TimeUnit. DAY -> timeSpan * TimeUtils.DAY
        }
    }

    /**
     * 毫秒时间戳转以unit为单位的时间长度
     *
     * @param millis 毫秒时间戳
     * @param unit   单位类型
     *
     *  * [TimeUtils.TimeUnit.MSEC]: 毫秒
     *  * [TimeUtils.TimeUnit.SEC]: 秒
     *  * [TimeUtils.TimeUnit.MIN]: 分
     *  * [TimeUtils.TimeUnit.HOUR]: 小时
     *  * [TimeUtils.TimeUnit.DAY]: 天
     *
     * @return 以unit为单位的时间长度
     */
    fun millis2TimeSpan(millis: Long, unit: TimeUtils.TimeUnit): Long {
        return when (unit) {
            TimeUtils.TimeUnit.MSEC -> millis
            TimeUtils.TimeUnit.SEC -> millis / TimeUtils.SEC
            TimeUtils.TimeUnit.MIN -> millis / TimeUtils.MIN
            TimeUtils.TimeUnit.HOUR -> millis / TimeUtils.HOUR
            TimeUtils.TimeUnit.DAY -> millis / TimeUtils.DAY
        }
    }

    /**
     * 毫秒时间戳转合适时间长度
     *
     * @param millis    毫秒时间戳
     *
     * 小于等于0，返回null
     * @param precision 精度
     *
     *  * precision = 0，返回null
     *  * precision = 1，返回天
     *  * precision = 2，返回天和小时
     *  * precision = 3，返回天、小时和分钟
     *  * precision = 4，返回天、小时、分钟和秒
     *  * precision >= 5，返回天、小时、分钟、秒和毫秒
     *
     * @return 合适时间长度
     */
    @SuppressLint("DefaultLocale")
    fun millis2FitTimeSpan(millis: Long, precision: Int): String? {
        var millis = millis
        var precision = precision
        if (millis <= 0 || precision <= 0) return null
        val sb = StringBuilder()
        val units = arrayOf("天", "小时", "分钟", "秒", "毫秒")
        val unitLen = intArrayOf(86400000, 3600000, 60000, 1000, 1)
        precision = Math.min(precision, 5)
        for (i in 0 until precision) {
            if (millis >= unitLen[i]) {
                val mode = millis / unitLen[i]
                millis -= mode * unitLen[i]
                sb.append(mode).append(units[i])
            }
        }
        return sb.toString()
    }
}