package com.example.mukfinalproject.model

import android.content.Context
import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mukfinalproject.util.MukFileUtils
import com.example.mukfinalproject.util.MukImageUtils
import java.util.*

@Entity
data class MukProfile(
    @PrimaryKey(autoGenerate = true) var mukId: Long? = null,
    var mukName: String = "",
    var mukGender: String = "",
    var mukCountry: String = "",
    var mukLatitude: Double = 0.0,
    var mukLongitude: Double = 0.0,
    var mukBirthday: Date = Date()
) {
    fun mukSaveImage(mukImage: Bitmap, mukContext: Context) {
        mukId?.let {
            MukImageUtils.mukSaveBitmapToFile(mukContext, mukImage, mukImageFileName(it))
        }
    }

    fun mukDeleteImage(mukContext: Context) {
        mukId?.let {
            MukFileUtils.mukDeleteFile(mukContext, mukImageFileName(it))
        }
    }

    companion object {
        fun mukImageFileName(mukId: Long): String {
            return "profile$mukId.png"
        }
    }
}