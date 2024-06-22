package com.tkw.domain.model

data class AlarmSettings(
    val ringToneMode: RingTone = RingTone.BELL,
    val alarmStartTime: String = "",
    val alarmEndTime: String = "",
    val alarmMode: AlarmMode = AlarmMode.Custom(listOf(), 0),
    val etcSetting: AlarmEtcSettings = AlarmEtcSettings()
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
    val stopReachedGoal: Boolean = false,
    val delayTomorrow: Boolean = false
)