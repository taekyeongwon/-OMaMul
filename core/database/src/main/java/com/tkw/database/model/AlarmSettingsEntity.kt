package com.tkw.database.model

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class AlarmSettingsEntity: RealmObject {
    @PrimaryKey
    var id: Int = DEFAULT_SETTING_ID
    private var ringToneMode: String = RingToneEntity.BELL.name
    private var alarmMode: String = AlarmModeEntity.PERIOD.name
    var etcSetting: AlarmEtcSettingsEntity? = null

    var ringToneEnum: RingToneEntity
        get() = RingToneEntity.valueOf(ringToneMode)
        set(value) {
            ringToneMode = value.name
        }
    var alarmModeEnum: AlarmModeEntity
        get() = AlarmModeEntity.valueOf(alarmMode)
        set(value) {
            alarmMode = value.name
        }

    companion object {
        const val DEFAULT_SETTING_ID = 0    //세팅은 하나만 존재하므로 0 고정
    }
}

enum class RingToneEntity {
    BELL, VIBE, ALL, IGNORE, DEVICE
}

enum class AlarmModeEntity {
    PERIOD, CUSTOM
}

class AlarmEtcSettingsEntity: EmbeddedRealmObject {
    var stopReachedGoal: Boolean = false
    var delayTomorrow: Boolean = false
}

class AlarmEntity: EmbeddedRealmObject {
    var alarmId: Int = -1
    var startTime: Long = -1L
    var enabled: Boolean = false
    var interval: Long = -1L
}