package com.android.jbig

import android.graphics.Bitmap

/**
 *  作者：dragon_ldg
 *  创建日期：2020/12/15
 *  描述：JBigConvert
 */
class JBigConvert {
    companion object {
        init {
            System.loadLibrary("JBigConvert")
        }
    }

    /**
     *  作者：dragon_ldg
     *  创建日期：2020/12/15
     *  描述：解压
     *
     * @param compressedBit 带解压字节数组
     * @return byte[] 解压后的字节数组
     */
    external fun decompressing(compressedBit: ByteArray?): ByteArray?

    /**
     *  作者：dragon_ldg
     *  创建日期：2020/12/15
     *  描述：压缩
     *
     * @param srcBit 待压缩的Bitmap
     * @param bitByte 待压缩Bitmap转换的字节数组，需要转换为适用C的数组
     * @return byte[] 压缩后的字节数组
     */
    external fun compressing(srcBit: Bitmap?, bitByte: ByteArray?): ByteArray?

    /**
     *  作者：dragon_ldg
     *  创建日期：2020/12/15
     *  描述：压缩
     *
     * @param bitWidth 待压缩的Bitmap的宽度
     * @param bitHeight 待压缩的Bitmap的高度
     * @param bitByte 待压缩Bitmap转换的字节数组，需要转换为适用C的数组
     * @return byte[] 压缩后的字节数组
     */
    external fun compressingWH(
        bitWidth: Int,
        bitHeight: Int,
        bitByte: ByteArray?
    ): ByteArray?
}