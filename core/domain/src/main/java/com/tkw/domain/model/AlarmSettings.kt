package com.tkw.domain.model

import java.time.LocalTime
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
    data class Period(
        val selectedDate: List<Int> = listOf(1, 2, 3, 4, 5),
        val interval: Long = 1000 * 60 * 60,
        val alarmStartTime: LocalTime = LocalTime.of(11,0),
        val alarmEndTime: LocalTime = LocalTime.of(22, 0),
        val alarm: Alarm = Alarm(0, 0, false)
    ): AlarmMode() {
        fun getAlarmTime(): String {
            val formatter = DateTimeFormatter.ofPattern("a hh:mm")
            val startTime = alarmStartTime.format(formatter)
            val endTime = alarmEndTime.format(formatter)

            return "$startTime - $endTime"
        }
    }

    data class Custom(
        val selectedDate: List<Int> = listOf(1, 2, 3, 4, 5),
        val alarmList: List<Alarm> = listOf()
    ): AlarmMode()
}

data class AlarmEtcSettings(
    val stopReachedGoal: Boolean = false,
    val delayTomorrow: Boolean = false
)

data class Alarm(
    val alarmId: Int,   //period인 경우 0 고정, custom인 경우 HHmm값으로.
    val startTime: Long,
    val enabled: Boolean
)