package com.tkw.omamul.common.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

object DateTimeUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getToday(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        return current.format(formatter)
    }

    fun getFormattedTime(date: String): String? {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val parsedDate = dateFormat.parse(date)
        if(parsedDate != null) {
            val calendar = Calendar.getInstance()
            calendar.time = parsedDate
            return getFormattedTime(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE)
            )
        }
        return null
    }
    fun getFormattedTime(hour: Int, minute: Int): String {
        val sdf = SimpleDateFormat("a hh:mm", Locale.getDefault())
        return sdf.format(getCalendar(hour, minute).time)
    }

    private fun getCalendar(hour: Int, minute: Int): Calendar {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return calendar
    }

    fun getTimeFromFormat(formatted: String): Calendar? {
        var calendar: Calendar? = null
        val sdf = SimpleDateFormat("a hh:mm", Locale.getDefault())
        val parsingDate = sdf.parse(formatted)
        if (parsingDate != null) {
            calendar = Calendar.getInstance()
            calendar.time = parsingDate
        }
        return calendar

    }
}