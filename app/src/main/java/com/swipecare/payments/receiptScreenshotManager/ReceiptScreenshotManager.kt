package com.swipecare.payments.receiptScreenshotManager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class ReceiptScreenshotManager {
    companion object {
        fun saveToInternalStorage(
            context: Context,
            bitmapImage: Bitmap,
            directoryName: String,
            imageFileName: String,
        ): String {
            val directory = context.getDir(directoryName, Context.MODE_PRIVATE)
            val imageFile = File(directory, imageFileName)

            FileOutputStream(imageFile).use { fos ->
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
//            context.openFileOutput(imageFileName,Context.MODE_PRIVATE).use { fos ->
//            }
            return context.filesDir.absolutePath
        }

        fun getImageFromInternalStorage(context: Context, directoryName: String,imageFileName: String): Bitmap? {
            return try {
                val directory = context.filesDir
                val file = File(context.getDir(directoryName,Context.MODE_PRIVATE), imageFileName)
                BitmapFactory.decodeStream(FileInputStream(file))
            } catch (e: Exception) {
                null
            }
        }
    }
}