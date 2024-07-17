package com.tkw.database.model

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class AlarmSettingsEntity: RealmObject {
    @PrimaryKey
    var id: Int = DEFAULT_SETTING_ID
    var ringToneMode: RingToneModeEntity? = null
    private var alarmMode: String = AlarmModeEntity.PERIOD.name
    var etcSetting: AlarmEtcSettingsEntity? = null

    var alarmModeEnum: AlarmModeEntity
        get() = AlarmModeEntity.valueOf(alarmMode)
        set(value) {
            alarmMode = value.name
        }

    companion object {
        const val DEFAULT_SETTING_ID = 0    //세팅은 하나만 존재하므로 0 고정
    }
}

class RingToneModeEntity: EmbeddedRealmObject {
    var isBell: Boolean = false
    var isVibe: Boolean = false
    var isDevice: Boolean = false
    var isSilence: Boolean = false
}

enum class AlarmModeEntity {
    PERIOD, CUSTOM
}

class AlarmEtcSettingsEntity: EmbeddedRealmObject {
    var stopReachedGoal: Boolean = false
    var delayTomorrow: Boolean = false
}