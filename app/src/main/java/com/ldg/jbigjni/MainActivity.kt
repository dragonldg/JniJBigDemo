package com.ldg.jbigjni

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.android.customeview.CHexConver
import com.android.customeview.CharUtils
import com.android.customeview.SignFiles
import com.android.customeview.SignaturePad
import com.android.jbig.JBigConvert

class MainActivity : AppCompatActivity(), SignaturePad.OnSignedListener {

    @BindView(R.id.signaturePad)
    lateinit var pad: SignaturePad

    @BindView(R.id.save)
    lateinit var save:Button

    @BindView(R.id.clear)
    lateinit var clear:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        pad.setOnSignedListener(this)
        setBtnState(false)
    }

    /**
     * 签名回调
     */
    override fun onSigned() {
        Log.d("LDG","onSigned...........")
        setBtnState(true)
    }

    /**
     * 清除签名回调
     */
    override fun onClear() {
        Log.d("LDG","onClear...........")
        setBtnState(false)
    }

    @OnClick(R.id.save, R.id.clear)
    fun onClick(v: View?) {
        when(v!!.id){
            R.id.save -> {
                Log.d("LDG", "save")
                createSignatureImage()
            }
            R.id.clear -> {
                Log.d("LDG", "clear")
                clearSignature()
            }
        }
    }

    /**
     * 签名并压缩保存
     */
    private fun createSignatureImage() {
        val signatureBitmap: Bitmap = pad.getSignatureBitmap()
        SignFiles.createSignFile(signatureBitmap)//存储原图片
        val data3: String? = CharUtils.jBitByteToCBitByte(signatureBitmap) //转换
        Log.d("LDG", "压缩前：${data3!!.length}====$data3")
        val sendBitmap: ByteArray? = CharUtils.hexString2ByteArray(data3)
        val bigConvert = JBigConvert()
        val compressData: ByteArray? = bigConvert.compressingWH(320, 144, sendBitmap)

        if (compressData == null || compressData.isEmpty()) {
            Toast.makeText(this, "out电子签名保存失败，请重新签名!", Toast.LENGTH_SHORT).show()
            return
        }
        val compressStr: String = CHexConver.bytesToHexString(compressData)
        Log.d("LDG", "压缩后：$compressStr====${compressStr.length}")
        if (compressStr.length > 999) {
            Toast.makeText(this, "签名字体过大，请重新签名!", Toast.LENGTH_SHORT).show()
            clearSignature()
            return
        } else
            Toast.makeText(this, "压缩签名成功!", Toast.LENGTH_SHORT).show()
    }

    /**
     * 清空签名
     */
    private fun clearSignature() {
        pad.clear()
    }

    /**
     * 按钮状态
     */
    private fun setBtnState(state:Boolean){
        save.isEnabled = state
        clear.isEnabled = state
    }
}
