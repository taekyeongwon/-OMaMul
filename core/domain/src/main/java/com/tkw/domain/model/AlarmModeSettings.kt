package com.tkw.domain.model

import java.time.LocalTime
import java.time.format.DateTimeFormatter

sealed class AlarmModeSetting {
    companion object {
        const val DEFAULT_PERIOD_INTERVAL: Long = 1000 * 60 * 60
        const val DEFAULT_CUSTOM_INTERVAL: Long = 1000 * 60 * 5 //임시 5분 처리. 추후 24시간으로 변경
    }
    data class Period(
        val selectedDate: List<Int> = listOf(),
        val interval: Long = DEFAULT_PERIOD_INTERVAL,
        val alarmStartTime: LocalTime = LocalTime.of(11,0),
        val alarmEndTime: LocalTime = LocalTime.of(22, 0),
        val alarmList: List<Alarm> = listOf()
    ): AlarmModeSetting() {
        fun getAlarmTimeRange(): String {
            val formatter = DateTimeFormatter.ofPattern("a hh:mm")
            val startTime = alarmStartTime.format(formatter)
            val endTime = alarmEndTime.format(formatter)

            return "$startTime - $endTime"
        }
    }

    data class Custom(
        val selectedDate: List<Int> = listOf(),
        val interval: Long = DEFAULT_CUSTOM_INTERVAL,
        val alarmList: List<Alarm> = listOf()
    ): AlarmModeSetting()
}