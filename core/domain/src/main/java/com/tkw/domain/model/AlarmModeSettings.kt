package com.tkw.domain.model

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class AlarmModeSetting(
    val selectedDate: List<Int> = listOf(),
    val interval: Long = DEFAULT_PERIOD_INTERVAL
) {
    companion object {
        const val DEFAULT_PERIOD_INTERVAL: Long = 1000 * 60 * 2 //한 시간으로 변경 필요. period 수정할 때 설정한 알람 간격 값임.
        const val DEFAULT_CUSTOM_INTERVAL: Long = 1000 * 60 * 5 //임시 5분 처리. 추후 24시간으로 변경
    }
}

data class Alarm(
    val alarmId: Int,   //HHmm
    val startTime: Long,
    val interval: Long = -1,
    val weekList: List<DayOfWeek>,
    val enabled: Boolean = false
) {
    var isChecked: Boolean = false

    fun getAlarmTime(): String {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        val instant = Instant.ofEpochMilli(startTime)
        val localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime()

        return localTime.format(formatter)
    }
}

data class AlarmList(
    val alarmList: List<Alarm> = listOf()
)