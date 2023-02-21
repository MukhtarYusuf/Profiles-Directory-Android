package com.example.mukfinalproject.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

object MukImageUtils {
    fun mukSaveBitmapToFile(mukContext: Context, mukBitmap: Bitmap, mukFilename: String) {
        val mukStream = ByteArrayOutputStream()
        mukBitmap.compress(Bitmap.CompressFormat.PNG, 100, mukStream)

        val mukBytes = mukStream.toByteArray()
        mukSaveBytesToFile(mukContext, mukBytes, mukFilename)
    }

    fun mukLoadBitmapFromFile(mukContext: Context, mukFilename: String): Bitmap? {
        val mukFilePath = File(mukContext.filesDir, mukFilename).absolutePath
        return BitmapFactory.decodeFile(mukFilePath)
    }

    private fun mukSaveBytesToFile(mukContext: Context, mukBytes: ByteArray, mukFilename: String) {
        val mukOutputStream: FileOutputStream
        try {
            mukOutputStream = mukContext.openFileOutput(mukFilename, Context.MODE_PRIVATE)
            mukOutputStream.write(mukBytes)
            mukOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Create a Unique File For Image
    fun mukCreateUniqueImageFile(mukContext: Context): File {
        val mukTimeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val mukFilename = "Profile_$mukTimeStamp"
        val mukFilesDir = mukContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile(mukFilename, ".jpg", mukFilesDir)
    }

    // Calculate Required inSampleSize for BitmapFactory Decoder
    private fun mukCalculateInSampleSize(width: Int, height: Int, reqWidth: Int, reqHeight: Int): Int {
        var mukInSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val mukHalfHeight = height / 2
            val mukHalfWidth = width / 2
            while (mukHalfHeight / mukInSampleSize >= reqHeight &&
                    mukHalfWidth / mukInSampleSize >= reqWidth) {
                mukInSampleSize *= 2
            }
        }

        return mukInSampleSize
    }

    // Decode a Bitmap Image From a File
    fun mukDecodeFileToSize(mukFilePath: String, mukWidth: Int, mukHeight: Int): Bitmap {
        val mukOptions = BitmapFactory.Options()
        mukOptions.inJustDecodeBounds = true

        BitmapFactory.decodeFile(mukFilePath, mukOptions)
        mukOptions.inSampleSize = mukCalculateInSampleSize(mukOptions.outWidth, mukOptions.outHeight,
            mukWidth, mukHeight)
        mukOptions.inJustDecodeBounds = false

        return BitmapFactory.decodeFile(mukFilePath, mukOptions)
    }

    // Decode a Bitmap Image From a Uri
    fun mukDecodeUriStreamToSize(mukUri: Uri, mukWidth: Int, mukHeight: Int, mukContext: Context): Bitmap? {
        var mukInputStream: InputStream? = null
        try {
            val mukOptions: BitmapFactory.Options
            mukInputStream = mukContext.contentResolver.openInputStream(mukUri)
            if (mukInputStream != null) {
                mukOptions = BitmapFactory.Options()
                mukOptions.inJustDecodeBounds = true
                BitmapFactory.decodeStream(mukInputStream, null, mukOptions)
                mukInputStream.close()

                mukInputStream = mukContext.contentResolver.openInputStream(mukUri)
                if (mukInputStream != null) {
                    mukOptions.inSampleSize = mukCalculateInSampleSize(mukOptions.outWidth, mukOptions.outHeight,
                        mukWidth, mukHeight)
                    mukOptions.inJustDecodeBounds = false
                    val mukBitmap = BitmapFactory.decodeStream(mukInputStream, null, mukOptions)
                    mukInputStream.close()

                    return mukBitmap
                }
            }

            return null
        } catch (e: Exception) {
            return null
        } finally {
            mukInputStream?.close()
        }
    }

}