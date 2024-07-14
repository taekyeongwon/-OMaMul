package com.tkw.domain.model

import java.io.Serializable
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

data class AlarmSettings(
    val ringToneMode: RingToneMode = RingToneMode(),
    val alarmMode: AlarmMode = AlarmMode.PERIOD,
    val etcSetting: AlarmEtcSettings = AlarmEtcSettings()
) {

}

data class RingToneMode(
    val isBell: Boolean = false,
    val isVibe: Boolean = false,
    val isDevice: Boolean = false,
    val isSilence: Boolean = false
) {
    fun getCurrentMode(): RingTone {
        return when {
            isBell && isVibe -> RingTone.ALL
            isBell -> RingTone.BELL
            isVibe -> RingTone.VIBE
            isDevice -> RingTone.DEVICE
            else -> RingTone.IGNORE
        }
    }
}

enum class RingTone {
    BELL, VIBE, ALL, IGNORE, DEVICE
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