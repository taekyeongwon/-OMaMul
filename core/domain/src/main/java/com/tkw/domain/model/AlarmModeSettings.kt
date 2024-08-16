package com.tkw.domain.model

import java.io.Serializable
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class AlarmModeSetting(
    val startTime: LocalTime = LocalTime.of(8, 0),
    val endTime: LocalTime = LocalTime.of(22, 0),
    val selectedDate: List<DayOfWeek> = listOf(),
    val interval: Int = DEFAULT_PERIOD_INTERVAL
) {
    companion object {
        const val DEFAULT_PERIOD_INTERVAL: Int = 60 * 60    //interval의 경우 toSecondOfDay와 ofSecondOfDay 사용하므로 밀리초 단위 제거
    }

    fun getTimeRange(): String {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        return "${startTime.format(formatter)} - ${endTime.format(formatter)}"
    }
}

data class Alarm(
    val alarmId: String,   //yyMMddHHmmss   DateTimeUtils - getDateTimeInt()
    val startTime: Long,
    val weekList: List<DayOfWeek>,
    val enabled: Boolean = false
): Serializable {
    var isChecked: Boolean = false

    fun copy(): Alarm = Alarm(alarmId, startTime, weekList, enabled).apply {
        isChecked = this@Alarm.isChecked
    }

    fun getAlarmTime(): String {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        val instant = Instant.ofEpochMilli(startTime)
        val localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime()

        return localTime.format(formatter)
    }

    fun getIntervalByNextDayOfWeek(): Int {
        val dateFromStartTime =
            Instant
                .ofEpochMilli(startTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()

        val currentDOW = DayOfWeek.from(dateFromStartTime)
        val daysDifference = runCatching {
            weekList.minOf {
                if(it.value == currentDOW.value) {
                    if(startTime > System.currentTimeMillis()) 0
                    else 7
                } else {
                    (it.value - currentDOW.value + 7) % 7
                }
            }
        }.getOrNull()

        return if(daysDifference == null) -1
        else 1000 * 60 * 60 * 24 * daysDifference
    }

    fun setLocalDateTimeTo(date: LocalDate): Long {
        val dateFromStartTime =
            Instant
                .ofEpochMilli(startTime)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()

        return dateFromStartTime.with(date)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}

data class AlarmList(
    val alarmList: List<Alarm> = listOf()
)