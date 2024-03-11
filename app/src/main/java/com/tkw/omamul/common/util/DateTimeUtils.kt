package com.tkw.omamul.common.util

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object DateTimeUtils {

    fun getToday(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return current.format(formatter)
    }

    fun getFormattedTime(date: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return getFormattedTime(
            LocalDateTime.parse(date, formatter).hour,
            LocalDateTime.parse(date, formatter).minute
        )
    }

    fun getFormattedTime(hour: Int, minute: Int): String {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        LocalTime.of(hour, minute).format(formatter)
        return LocalTime.of(hour, minute).format(formatter)
    }

    fun getTimeFromFormat(formatted: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        return LocalTime.parse(formatted, formatter)
    }
}