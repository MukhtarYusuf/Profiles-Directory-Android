package com.example.mukfinalproject.util

import java.text.DateFormat
import java.util.*

object MukDateUtils {
    fun mukDateToString(mukDate: Date): String {
        val mukOutputFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        return mukOutputFormat.format(mukDate)
    }

    fun mukStringToDate(mukDateString: String): Date {
        val mukInputFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        return mukInputFormat.parse(mukDateString) as Date
    }
}