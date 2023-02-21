package com.example.mukfinalproject.util

import android.content.Context
import java.io.File

object MukFileUtils {
    fun mukDeleteFile(mukContext: Context, mukFileName: String) {
        val mukDir = mukContext.filesDir
        val mukFile = File(mukDir, mukFileName)
        mukFile.delete()
    }
}