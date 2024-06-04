package com.tkw.domain.model

data class AlarmSettings(
    val ringToneMode: RingTone,
    val alarmStartTime: String,
    val alarmEndTime: String,
    val alarmMode: AlarmMode,
    val etcSetting: AlarmEtcSettings
) {

}

enum class RingTone {
    BELL, VIBE, ALL, IGNORE
}

sealed class AlarmMode {
    data class Period(
        val selectedDate: List<Int>,
        val interval: Long
    ): AlarmMode()

    data class Custom(
        val selectedDate: List<Int>,
        val interval: Long
    ): AlarmMode()
}

data class AlarmEtcSettings(
    val stopReachedGoal: Boolean,
    val delayTomorrow: Boolean
)