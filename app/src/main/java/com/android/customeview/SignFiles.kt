package com.android.customeview

import android.graphics.*
import android.os.Environment
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SignFiles {
    @Throws(IOException::class)
    fun compressAndGenImage(bitmap: Bitmap, outPath: String?, maxSize: Int): String? {
        var baos: ByteArrayOutputStream? = null
        var _path: String? = null
        try {
            val bitmapTag = Bitmap.createBitmap(
                bitmap.width,
                bitmap.height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmapTag)
            val paint = Paint()
            canvas.drawColor(Color.parseColor("#ffffff"))
            canvas.drawBitmap(
                bitmap, null, Rect(
                    0, 0, bitmap.width,
                    bitmap.height
                ), paint
            )
            canvas.save() //Canvas.ALL_SAVE_FLAG
            canvas.restore()
            _path = (createSignFileDir() + "/"
                    + System.currentTimeMillis() + ".jpeg")
            baos = ByteArrayOutputStream()
            // scale
            var options = 100
            bitmapTag.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val photoBytes = baos.toByteArray()
            while (baos.toByteArray().size / 1024 > maxSize) {
                // Clean up os
                baos.reset()
                // interval 10
                options -= 10
                bitmapTag.compress(Bitmap.CompressFormat.JPEG, options, baos)
            }
            if (photoBytes != null) {
                FileOutputStream(File(_path)).write(photoBytes)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                baos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return _path
    }

    companion object {
        var signPhotoPath = Environment
            .getExternalStorageDirectory().absolutePath + "/ASignPhoto"

        /*
	 * 在内存中创建文件夹
	 */
        fun createSignFileDir(): String {
            Log.d("LDG", "待创建目录：$signPhotoPath")
            val photosFile= File(signPhotoPath)
            if(!photosFile.exists()){
                photosFile.mkdirs()
                Log.d("LDG","文件夹已经创建")
            }else
                Log.d("LDG",signPhotoPath+"文件夹已经存在")
            val cmd = "chmod 777 " + photosFile.absolutePath
            try {
                Runtime.getRuntime().exec(cmd)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return signPhotoPath
        }

        fun createSignFile(bitmap: Bitmap): String? {
            var baos: ByteArrayOutputStream? = null
            var _path: String? = null
            try {
                val bitmapTag = Bitmap.createBitmap(
                    bitmap.width,
                    bitmap.height, Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmapTag)
                val paint = Paint()
                canvas.drawColor(Color.parseColor("#ffffff"))
                canvas.drawBitmap(
                    bitmap, null, Rect(
                        0, 0, bitmap.width,
                        bitmap.height
                    ), paint
                )
                canvas.save() //Canvas.ALL_SAVE_FLAG
                canvas.restore()
                _path = createSignFileDir() + "/signDemo.jpeg"
                val tempFile = File(_path)
                if (tempFile.exists()) {
                    tempFile.delete()
                }
                tempFile.createNewFile()
                baos = ByteArrayOutputStream()
                bitmapTag.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val photoBytes = baos.toByteArray()
                FileOutputStream(tempFile).write(photoBytes)
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    baos?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return _path
        }

        fun deleteAll(f: File) {
            // 文件
            if (f.isFile) {
                f.delete()
            } else { // 文件夹
                // 获得当前文件夹下的所有子文件和子文件夹
                val f1 = f.listFiles()
                // 循环处理每个对象
                val len = f1.size
                for (i in 0 until len) {
                    // 递归调用，处理每个文件对象
                    deleteAll(f1[i])
                }
                // 删除当前文件夹
                f.delete()
            }
        }

        fun getSuitBitmap(
            resPath: String?, reqWidth: Int,
            reqHeight: Int
        ): Bitmap? {
            if (CHexConver.isEmpty(resPath)) {
                return null
            }
            val file = File(resPath)
            if (file.exists()) {
                // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeFile(resPath, options)
                // 调用上面定义的方法计算inSampleSize值
                options.inSampleSize = calculateInSampleSize(
                    options, reqWidth,
                    reqHeight
                )
                // 使用获取到的inSampleSize值再次解析图片
                options.inJustDecodeBounds = false
                return BitmapFactory.decodeFile(resPath, options)
            }
            return null
        }

        fun calculateInSampleSize(
            options: BitmapFactory.Options,
            reqWidth: Int, reqHeight: Int
        ): Int {
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1
            if (height > reqHeight || width > reqWidth) {
                // 计算出实际宽高和目标宽高的比率
                val heightRatio = Math.round(
                    height.toFloat()
                            / reqHeight.toFloat()
                )
                val widthRatio =
                    Math.round(width.toFloat() / reqWidth.toFloat())
                // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
                // 一定都会大于等于目标的宽和高。
                inSampleSize = if (heightRatio > widthRatio) heightRatio else widthRatio
            }
            return inSampleSize
        }
    }
}