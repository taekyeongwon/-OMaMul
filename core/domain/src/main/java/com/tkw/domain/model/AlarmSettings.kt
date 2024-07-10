package com.tkw.domain.model

import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class AlarmSettings(
    val ringToneMode: RingTone = RingTone.BELL,
    val alarmMode: AlarmMode = AlarmMode.PERIOD,
    val etcSetting: AlarmEtcSettings = AlarmEtcSettings()
) {

}

enum class RingTone {
    BELL, VIBE, ALL, IGNORE
}

enum class AlarmMode {
    PERIOD, CUSTOM
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