package com.android.customeview

import android.graphics.Bitmap
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.*

/**
 *  作者：dragon_ldg
 *  创建日期：2020/12/15
 *  描述：CharUtils
 */
object CharUtils {
    /**
     * 作者：dragon_ldg
     * 创建日期：2018/12/5 14:28
     * 描述：java bitmap 对象转C bitmap对象
     * "00000000":RGBA，仅考虑A的值，RGB最终都会转为黑色
     */
    fun jBitByteToCBitByte(bitmap: Bitmap?): String? {
        val bitBytes: ByteArray? = getBytesByBitmap(bitmap!!)
        val bitStr: String? = bytesToHexString(bitBytes!!)
        val sbOut = StringBuilder()
        val count = bitStr!!.length / 64
        for (j in 0 until count) {
            val sb = StringBuilder()
            var i = j * 64
            while (i < (j + 1) * 64) {
                if ("00000000" == bitStr.substring(i, i + 8)) { //只要带颜色的就转为黑色，即透明度不为00，不用刻意区分是什么颜色
                    sb.append("0")
                } else sb.append("1")
                i += 8
            }
            val da: String? = binaryToHex(sb.toString())
            sbOut.append(da)
        }
        return sbOut.toString()
    }

    /**
     * 作者：dragon_ldg
     * 创建日期：2018/12/5 14:35
     * 描述：C压缩对象抓为Java可编码为Bitmap的对象
     */
    fun cBitByteToJBitByte(cString: String): String? {
        val sbOut = StringBuilder()
        val cLen = cString.length
        val sbIn = StringBuilder()
        for (i in 0 until cLen) {
            if ("0" == cString.substring(i, i + 1)) {
                sbIn.append("0000")
            } else {
                sbIn.append(hexToBinary(
                        cString.substring(
                            i,
                            i + 1
                        )
                    )
                )
            }
        }
        val tempStr = sbIn.toString()
        val tempLen = tempStr.length
        for (j in 0 until tempLen) {
            if ("0" == tempStr.substring(j, j + 1)) {
                sbOut.append("00000000")
            } else {
                sbOut.append("000000FF")
            }
        }
        return sbOut.toString()
    }

    /**
     * 将16进制字符串转换为byte[]
     *
     * @param bs
     * @return
     */
    fun hexString2ByteArray(bs: String?): ByteArray? {
        if (bs == null) {
            return null
        }
        val bsLength = bs.length
        if (bsLength % 2 != 0) {
            return null
        }
        val cs = ByteArray(bsLength / 2)
        var st: String
        var i = 0
        while (i < bsLength) {
            st = bs.substring(i, i + 2)
            cs[i / 2] = st.toInt(16).toByte()
            i += 2
        }
        return cs
    }

    private fun getBytesByBitmap(bitmap: Bitmap): ByteArray? {
        val bytes = bitmap.byteCount
        val buf = ByteBuffer.allocate(bytes)
        bitmap.copyPixelsToBuffer(buf)
        return buf.array()
    }

    private fun bytesToHexString(bArray: ByteArray): String? {
        val sb = java.lang.StringBuilder(bArray.size)
        var sTemp: String
        for (aBArray in bArray) {
            sTemp = Integer.toHexString(0xFF and aBArray.toInt())
            if (sTemp.length < 2) sb.append(0)
            sb.append(sTemp.toUpperCase(Locale.getDefault()))
        }
        return sb.toString()
    }

    /**
     * 作者：dragon_ldg
     * 创建日期：2018/12/4 17:13
     * 描述：二进制字符串转十六进制字符串
     */
    private fun binaryToHex(binarySource: String): String? {
        val bi = BigInteger(binarySource, 2) //转换为BigInteger类型
        var hexStr = Integer.toHexString(bi.toInt())
        if (hexStr.length < 2) hexStr = "0$hexStr"
        return hexStr.toUpperCase(Locale.getDefault()) //转换成十六进制
    }

    private fun hexToBinary(hexSource: String): String? {
        val bi = BigInteger(hexSource, 16) //转换为BigInteger类型
        var hexStr = Integer.toBinaryString(bi.toInt())
        when (hexStr.length) {
            1 -> hexStr = "000$hexStr"
            2 -> hexStr = "00$hexStr"
            3 -> hexStr = "0$hexStr"
        }
        return hexStr
    }
}