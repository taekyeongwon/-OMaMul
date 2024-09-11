package com.tkw.common.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

sealed class DateTimeUtils {
    object Date {
        const val DATE_PATTERN = "yyyy-MM-dd"
        fun getToday(): String {
            val current = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
            return current.format(formatter)
        }

        fun getFormat(date: LocalDate): String {
            val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
            return date.format(formatter)
        }

        fun getLocalDate(formatted: String): LocalDate {
            val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)
            return LocalDate.parse(formatted, formatter)
        }
    }

    object Time {
        const val TIME_PATTERN = "a hh:mm"
        fun getFormat(timeInMillis: Long): String {
            val formatter = DateTimeFormatter.ofPattern(TIME_PATTERN)
            return getLocalTime(timeInMillis).format(formatter)
        }

        fun getFormat(hour: Int, minute: Int): String {
            val formatter = DateTimeFormatter.ofPattern(TIME_PATTERN)
            return LocalTime.of(hour, minute).format(formatter)
        }

        fun getFormat(hour: Int, minute: Int, hourFormat: String, minFormat: String): String {
            val formatter = DateTimeFormatter
                .ofPattern("H'$hourFormat' mm'$minFormat'", Locale.getDefault())

            return LocalTime.of(hour, minute).format(formatter)
        }

        fun getFormat(timeInSecond: Long, hourFormat: String, minFormat: String): String {
            val formatter = DateTimeFormatter
                .ofPattern("H'$hourFormat' mm'$minFormat'", Locale.getDefault())

            return LocalTime.ofSecondOfDay(timeInSecond).format(formatter)
        }

        fun getLocalTime(timeInMillis: Long): LocalTime {
            return Instant.ofEpochMilli(timeInMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
        }

        fun getLocalTime(hour: Int, minute: Int): LocalTime {
            return LocalTime.of(hour, minute)
        }

        fun getLocalTime(formatted: String, pattern: String): LocalTime {
            val formatter = DateTimeFormatter.ofPattern(pattern)
            return LocalTime.parse(formatted, formatter)
        }

        fun getLocalTime(formatted: String, hourFormat: String, minFormat: String): LocalTime {
            val formatter = DateTimeFormatter
                .ofPattern("H'$hourFormat' mm'$minFormat'", Locale.getDefault())
            return LocalTime.parse(formatted, formatter)
        }
    }

    object DateTime {
        const val DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss"

        fun getToday(): String {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN)
            return current.format(formatter)
        }

        fun getTimeFormat(date: String): String {
            val formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN)
            return Time.getFormat(
                LocalDateTime.parse(date, formatter).hour,
                LocalDateTime.parse(date, formatter).minute
            )
        }

        fun getFormat(date: String): String {
            val formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN)
            return LocalDateTime.of(
                Date.getLocalDate(date),
                LocalTime.of(0, 0)
            ).format(formatter)
        }

        fun getFormat(dateTime: LocalDateTime): String {
            val formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN)
            return dateTime.format(formatter)
        }

        fun getFormat(dateTime: String, hour: Int, minute: Int): String {
            val formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN)
            return LocalDateTime.of(
                LocalDate.parse(dateTime, formatter),
                LocalTime.of(hour, minute)
            ).format(formatter)
        }

        fun getFormatTrim(timeInMillis: Long): String {
            val formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss")
            val dateTime = Instant.ofEpochMilli(timeInMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
            return dateTime.format(formatter)
        }

        fun getLocalDateTime(timeInMillis: Long): LocalDateTime {
            return Instant.ofEpochMilli(timeInMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }
    }
}

object DateTimeUtil {
    fun getWeekDates(date: String): Pair<String, String> {
        val formatter = DateTimeFormatter.ofPattern(DateTimeUtils.Date.DATE_PATTERN)
        val localDate = DateTimeUtils.Date.getLocalDate(date)
        val startOfWeek = localDate.with(DayOfWeek.MONDAY).format(formatter)
        val endOfWeek = localDate.with(DayOfWeek.SUNDAY).format(formatter)
        return startOfWeek to endOfWeek
    }

    fun getIndexOfWeek(date: String): Int {
        val localDate = DateTimeUtils.Date.getLocalDate(date)
        return localDate.dayOfWeek.value
    }

    fun getMonthDates(date: String): Pair<String, String> {
        val formatter = DateTimeFormatter.ofPattern(DateTimeUtils.Date.DATE_PATTERN)
        val localDate = DateTimeUtils.Date.getLocalDate(date)
        val startOfMonth = localDate.withDayOfMonth(1).format(formatter)
        val endOfMonth = localDate.withDayOfMonth(localDate.lengthOfMonth()).format(formatter)
        return startOfMonth to endOfMonth
    }
}

fun LocalTime.toEpochMilli(zoneId: ZoneId = ZoneId.systemDefault()): Long {
    val currentDate = LocalDate.now(zoneId)
    val instant = this.atDate(currentDate).atZone(zoneId).toInstant()
    return instant.toEpochMilli()
}