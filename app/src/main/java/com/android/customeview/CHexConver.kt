package com.android.customeview

import android.annotation.SuppressLint
import android.util.Log
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

object CHexConver {
    private val mChars = "0123456789ABCDEF".toCharArray()
    private const val mHexStr = "0123456789ABCDEF"

    /**
     * 检测是否为空
     *
     * @param st
     * @return
     */
    fun isEmpty(st: String?): Boolean {
        return st == null || st.length == 0
    }

    fun isEmpty(by: ByteArray?): Boolean {
        return by == null || by.size == 0
    }

    /**
     * 字符转ASC
     * @param st
     * @return
     */
    fun getAsc(st: String): Int {
        val gc = st.toByteArray()
        return gc[0].toInt()
    }

    /**
     * ASC转字符
     * @param backnum
     * @return
     */
    fun backchar(backnum: Int): Char {
        return backnum.toChar()
    }

    /**
     * String转ascii
     * @param st
     * @return
     */
    fun parseAscii(str: String): String {
        val sb = StringBuilder()
        val bs = str.toByteArray()
        for (i in bs.indices) sb.append(toHex(bs[i].toInt()))
        return sb.toString()
    }

    private fun toHex(n: Int): String {
        val sb = StringBuilder()
        if (n / 16 == 0) {
            return toHexUtil(n)
        } else {
            val t = toHex(n / 16)
            val nn = n % 16
            sb.append(t).append(toHexUtil(nn))
        }
        return sb.toString()
    }

    private fun toHexUtil(n: Int): String {
        var rt = ""
        when (n) {
            10 -> rt += "A"
            11 -> rt += "B"
            12 -> rt += "C"
            13 -> rt += "D"
            14 -> rt += "E"
            15 -> rt += "F"
            else -> rt += n
        }
        return rt
    }

    /**
     * 检查16进制字符串是否有效
     * @param String sHex 16进制字符串
     * @return boolean
     */
    @SuppressLint("DefaultLocale")
    fun checkHexStr(sHex: String): Boolean {
        val sTmp =
            sHex.trim { it <= ' ' }.replace(" ", "").toUpperCase(Locale.US)
        val sLen = sTmp.length
        return if (sLen > 1 && sLen % 2 == 0) {
            for (i in 0 until sLen) if (!mHexStr.contains(
                    sTmp.substring(
                        i,
                        i + 1
                    )
                )
            ) return false
            true
        } else false
    }

    /**
     * 字符串转换成十六进制字符串
     * @param String str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    fun str2HexStr(str: String): String {
        val sb = StringBuilder("")
        val bs = str.toByteArray()
        var bit: Int
        for (i in bs.indices) {
            bit = bs[i].toInt() and 0x0f0 shr 4
            sb.append(mChars[bit])
            bit = bs[i].toInt() and 0x0f
            sb.append(mChars[bit])
        }
        return sb.toString().trim { it <= ' ' }
    }

    /**
     * 十六进制字符串转换成 ASCII字符串
     * @param String str Byte字符串
     * @return String 对应的字符串
     */
    fun hexStr2Str(hexStr: String): String {
        var hexStr = hexStr
        hexStr =
            hexStr.trim { it <= ' ' }.replace(" ", "").toUpperCase(Locale.US)
        val hexs = hexStr.toCharArray()
        val bytes = ByteArray(hexStr.length / 2)
        var n: Int
        for (i in bytes.indices) {
            n = mHexStr.indexOf(hexs[2 * i]) * 16
            n += mHexStr.indexOf(hexs[2 * i + 1])
            bytes[i] = (n and 0xff).toByte()
        }
        return String(bytes)
    }

    /**
     * bytes转换成十六进制字符串
     * @param byte[] b byte数组
     * @param int iLen 取前N位处理 N=iLen
     * @return String 每个Byte值之间空格分隔
     * @throws Exception
     */
    @Throws(Exception::class)
    fun byte2HexStr(b: ByteArray?): String {
        return String(b!!, Charset.forName("GBK"))
    }

    /**
     * 获取数字字符串
     * @param byte[] b byte数组
     * @return String
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getNumber(b: ByteArray): String {
        var number = ""
        for (i in b.indices) {
            if (b[i].toInt() == 11) {
                number += "."
            } else {
                number += b[i]
            }
        }
        return number
    }

    /**
     * @param str 需要补齐的字符串
     * @param length 补齐后的字符串长度
     * @param addValue 需要补上的内容
     * @param way 补齐方式，左对齐 left 右对齐 right  默认右补齐
     * @return
     */
    fun endAddSpaceOrZore(
        str: String,
        length: Int,
        addValue: String,
        way: String
    ): String {
        var str = str
        if (str.length == length) {
            return str
        }
        while (str.length < length) {
            if ("right" == way) {
                str += addValue
            } else if ("left" == way) {
                str = addValue + str
            } else {
                str += addValue
            }
        }
        return str
    }

    private fun asc_to_bcd(asc: Byte): Byte {
        val bcd: Byte
        bcd =
            if (asc >= '0'.toByte() && asc <= '9'.toByte()) (asc - '0'.toByte()).toByte() else if (asc >= 'A'.toByte() && asc <= 'F'.toByte()) (asc - 'A'.toByte() + 10).toByte() else if (asc >= 'a'.toByte() && asc <= 'f'.toByte()) (asc - 'a'.toByte() + 10).toByte() else (asc - 48).toByte()
        return bcd
    }

    /**
     * bytes转换成十六进制字符串 ,2个byte转一个字符，类似BCD
     * @param String str Byte字符串
     * @return String 对应的字符串
     */
    @Throws(Exception::class)
    fun Bytes2ToHexStr(b: ByteArray): String {
        val bytes = ByteArray(b.size / 2)
        var n: Int
        for (i in bytes.indices) {
            n = asc_to_bcd(b[2 * i]) * 16
            n += asc_to_bcd(b[2 * i + 1]).toInt()
            bytes[i] = (n and 0xff).toByte()
        }
        return byte2HexStr(bytes)
    }

    /**
     * bytes字符串转换为Byte值
     * @param String src Byte字符串，每个Byte之间没有分隔符(字符范围:0-9 A-F)
     * @return byte[]
     */
    fun hexStr2Bytes(src: String): ByteArray {
        /* 对输入值进行规范化整理 */
        var src = src
        src = src.trim { it <= ' ' }.replace(" ", "").toUpperCase(Locale.US)
        // 处理值初始化
        var m = 0
        var n = 0
        val l = src.length / 2 // 计算长度
        val ret = ByteArray(l) // 分配存储空间
        for (i in 0 until l) {
            m = i * 2 + 1
            n = m + 1
            ret[i] = (Integer.decode(
                "0x" + src.substring(i * 2, m) + src.substring(
                    m,
                    n
                )
            ) and 0xFF).toByte()
        }
        return ret
    }

    /**
     * String的字符串转换成unicode的String
     * @param String strText 全角字符串
     * @return String 每个unicode之间无分隔符
     * @throws Exception
     */
    @Throws(Exception::class)
    fun strToUnicode(strText: String): String {
        var c: Char
        val str = StringBuilder()
        var intAsc: Int
        var strHex: String
        for (i in 0 until strText.length) {
            c = strText[i]
            intAsc = c.toInt()
            strHex = Integer.toHexString(intAsc)
            if (intAsc > 128) str.append("\\u$strHex") else  // 低位在前面补00
                str.append("\\u00$strHex")
        }
        return str.toString()
    }

    /**
     * unicode的String转换成String的字符串
     * @param String hex 16进制值字符串 （一个unicode为2byte）
     * @return String 全角字符串
     */
    fun unicodeToString(hex: String): String {
        val t = hex.length / 6
        val str = StringBuilder()
        for (i in 0 until t) {
            val s = hex.substring(i * 6, (i + 1) * 6)
            // 高位需要补上00再转
            val s1 = s.substring(2, 4) + "00"
            // 低位直接转
            val s2 = s.substring(4)
            // 将16进制的string转为int
            val n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16)
            // 将int转换为字符
            val chars = Character.toChars(n)
            str.append(String(chars))
        }
        return str.toString()
    }

    /**
     * Byte ascii转成int
     * @param b byte 值
     * @return int值
     */
    fun byteToInt(b: ByteArray): Int {
        val mask = 0xff
        var temp = 0
        var n = 0
        for (i in b.indices) {
            val vByte = asc_to_bcd(b[i])
            n = n shl 8
            temp = vByte.toInt() and mask
            n = n or temp
        }
        return n
    }

    /**
     * Byte 转成int
     * @param b byte 值
     * @return int值
     */
    fun byteToInt2(b: ByteArray): Int {
        val mask = 0xff
        var temp = 0
        var n = 0
        for (i in b.indices) {
            n = n shl 8
            temp = b[i].toInt() and mask
            n = n or temp
        }
        return n
    }

    fun byteToInt2(b: ByteArray, Offset: Int, Len: Int): Int {
        val mask = 0xff
        var temp = 0
        var n = 0
        for (i in Offset until Offset + Len) {
            n = n shl 8
            temp = b[i].toInt() and mask
            n = n or temp
        }
        return n
    }

    /**
     * 数字字符串转ASCII码字符串
     * @param String 字符串
     * @return ASCII字符串
     */
    fun byteToAsciiString(bytearray: ByteArray): String {
        var result = ""
        var temp: Char
        val length = bytearray.size
        for (i in 0 until length) {
            temp = bytearray[i].toChar()
            result += temp
        }
        return result
    }

    /**
     * byte流打印String
     * @param bArray byte
     * @return
     */
    @SuppressLint("DefaultLocale")
    fun bytesToHexString(bArray: ByteArray?): String {
        if (bArray == null) {
            return ""
        }
        val sb = StringBuffer(bArray.size)
        var sTemp: String
        for (i in bArray.indices) {
            sTemp = Integer.toHexString(0xFF and bArray[i].toInt())
            if (sTemp.length < 2) sb.append(0)
            sb.append(sTemp.toUpperCase())
        }
        return sb.toString()
    }

    /**
     * byte流打印String
     * @param bArray byte
     * @return
     */
    @SuppressLint("DefaultLocale")
    fun bytesToHexStringSpace(bArray: ByteArray?): String {
        if (bArray == null) {
            return ""
        }
        val sb = StringBuffer(bArray.size)
        var sTemp: String
        for (i in bArray.indices) {
            sTemp = Integer.toHexString(0xFF and bArray[i].toInt())
            if (sTemp.length < 2) sb.append(0)
            sb.append(sTemp.toUpperCase())
            sb.append(" ")
        }
        return sb.toString()
    }

    /**
     * byte流打印String
     * @param bArray byte
     * @return
     */
    @SuppressLint("DefaultLocale")
    fun bytesTo2String(bArray: ByteArray): String {
        val sb = StringBuffer(bArray.size)
        var sTemp: String
        for (i in bArray.indices) {
            sTemp = Integer.toBinaryString(0x000001 and bArray[i].toInt())
            if (sTemp.length < 7) sb.append(0)
            sb.append(sTemp.toUpperCase())
        }
        return sb.toString()
    }

    fun ByteArrayToBinaryString(byteArray: ByteArray): String {
        val capacity = byteArray.size * 8
        val sb = StringBuilder(capacity)
        Log.i("INFO", "签名数据长度" + byteArray.size)
        for (i in byteArray.indices) {
            val a = Integer.toBinaryString(byteArray[i].toInt()).length
            Log.i(
                "INFO",
                "第" + i + "个域长度---" + i + "个域数据---" + Integer.toBinaryString(
                    byteArray[i].toInt()
                )
            )
            for (ii in 0..8 - a) {
                sb.append(0)
            }
            sb.append(Integer.toBinaryString(byteArray[i].toInt()))
        }
        return sb.toString()
    }

    /**
     * 字符转byte[]
     * @param str 值
     * @param charEncode “GBK”
     * @return
     */
    fun StringToByte(str: String?, charEncode: String?): ByteArray? {
        var destObj: ByteArray? = null
        try {
            if (null == str || str == "") {
                destObj = ByteArray(0)
                return destObj
            } else {
                destObj = str.toByteArray(charset(charEncode!!))
            }
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return destObj
    }

    /**
     * 字符转byte[]
     * @param str 值
     * @param charEncode “GBK”
     * @return
     */
    fun StringToByteAmount(str: String?, charEncode: String?): ByteArray? {
        var str = str
        var destObj: ByteArray? = null
        try {
            if (null == str || str.trim { it <= ' ' } == "") {
                destObj = ByteArray(0)
                return destObj
            } else {
                str = str.replace(".", "")
                destObj = str.toByteArray(charset(charEncode!!))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return destObj
    }

    fun abcd_to_asc(ucBcd: Byte): Byte {
        var ucBcd = ucBcd
        val ucAsc: Byte
        ucBcd = (ucBcd.toInt() and 0x0f).toByte()
        ucAsc =
            if (ucBcd <= 9) (ucBcd + '0'.toByte()).toByte() else (ucBcd + 'A'.toByte() - 10.toByte()).toByte()
        return ucAsc
    }

    fun BcdToAsc(sAscBuf: ByteArray, sBcdBuf: ByteArray, iAscLen: Int) {
        var i: Int
        var j: Int
        j = 0
        i = 0
        while (i < iAscLen / 2) {
            sAscBuf[j] = (sBcdBuf[i].toInt() and 0xf0 shr 4) as Byte
            sAscBuf[j] = abcd_to_asc(sAscBuf[j])
            j++
            sAscBuf[j] = (sBcdBuf[i].toInt() and 0x0f) as Byte
            sAscBuf[j] = abcd_to_asc(sAscBuf[j])
            j++
            i++
        }
        if (iAscLen % 2 != 0) {
            sAscBuf[j] = (sBcdBuf[i].toInt() and 0xf0 shr 4) as Byte
            sAscBuf[j] = abcd_to_asc(sAscBuf[j])
        }
    }
}