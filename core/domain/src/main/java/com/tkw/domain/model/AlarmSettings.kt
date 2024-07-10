package com.tkw.domain.model

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

data class AlarmSettings(
    val ringToneMode: RingTone = RingTone.BELL,
    val alarmMode: AlarmMode = AlarmMode.Period(),
    val etcSetting: AlarmEtcSettings = AlarmEtcSettings()
) {

}

enum class RingTone {
    BELL, VIBE, ALL, IGNORE
}

sealed class AlarmMode {
    companion object {
        const val DEFAULT_PERIOD_INTERVAL: Long = 1000 * 60 * 60
        const val DEFAULT_CUSTOM_INTERVAL: Long = 1000 * 60 * 5 //임시 5분 처리. 추후 24시간으로 변경
    }
    data class Period(
        val selectedDate: List<Int> = listOf(1, 2, 3, 4, 5),
        val interval: Long = DEFAULT_PERIOD_INTERVAL,
        val alarmStartTime: LocalTime = LocalTime.of(11,0),
        val alarmEndTime: LocalTime = LocalTime.of(22, 0),
        val alarmList: List<Alarm> = listOf()
    ): AlarmMode() {
        fun getAlarmTimeRange(): String {
            val formatter = DateTimeFormatter.ofPattern("a hh:mm")
            val startTime = alarmStartTime.format(formatter)
            val endTime = alarmEndTime.format(formatter)

            return "$startTime - $endTime"
        }
    }

    data class Custom(
        val selectedDate: List<Int> = listOf(1, 2, 3, 4, 5),
        val interval: Long = DEFAULT_CUSTOM_INTERVAL,
        val alarmList: List<Alarm> = listOf()
    ): AlarmMode()
}

data class AlarmEtcSettings(
    val stopReachedGoal: Boolean = false,
    val delayTomorrow: Boolean = false
)

data class Alarm(
    val alarmId: Int,   //HHmm
    val startTime: Long,
    val enabled: Boolean,
    val interval: Long = -1L
) {
    var isChecked: Boolean = false

    fun getAlarmTime(): String {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        val instant = Instant.ofEpochMilli(startTime)
        val localTime = instant.atZone(ZoneId.systemDefault()).toLocalTime()

        return localTime.format(formatter)
    }
}