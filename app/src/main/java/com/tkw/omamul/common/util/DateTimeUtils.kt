package com.tkw.omamul.common.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object DateTimeUtils {

    fun getToday(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return current.format(formatter)
    }

    fun getTodayDate(): String {
        val current = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return current.format(formatter)
    }

    fun getFormattedTime(date: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return getFormattedTime(
            LocalDateTime.parse(date, formatter).hour,
            LocalDateTime.parse(date, formatter).minute
        )
    }

    fun getFullFormatFromDateTime(dateTime: String, hour: Int, minute: Int): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDateTime.of(
            getDateFromFullFormat(dateTime),
            LocalTime.of(hour, minute)
        ).format(formatter)
    }

    fun getFullFormatFromDate(date: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDateTime.of(
            getDateFromFormat(date),
            LocalTime.of(0, 0)
        ).format(formatter)
    }

    fun getFormattedTime(hour: Int, minute: Int): String {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        return LocalTime.of(hour, minute).format(formatter)
    }

    fun getDateFromFullFormat(formatted: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalDate.parse(formatted, formatter)
    }

    fun getDateFromFormat(formatted: String): LocalDate {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return LocalDate.parse(formatted, formatter)
    }

    fun getTimeFromFullFormat(formatted: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalTime.parse(formatted, formatter)
    }

    fun getTimeFromFormat(formatted: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        return LocalTime.parse(formatted, formatter)
    }
}