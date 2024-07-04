package com.tkw.database.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class AlarmSettingsEntity: RealmObject {
    @PrimaryKey
    var id: Int = 0 //세팅은 하나만 존재하므로 0 고정
    private var ringToneMode: String = RingToneEntity.BELL.state
    var ringToneEnum: RingToneEntity
        get() = RingToneEntity.valueOf(ringToneMode)
        set(value) {
            ringToneMode = value.state
        }
    var alarmMode: AlarmModeEntity? = null
    var etcSetting: AlarmEtcSettingsEntity? = null
}

enum class RingToneEntity(var state: String) {
    BELL("BELL"), VIBE("VIBE"), ALL("ALL"), IGNORE("IGNORE")
}

open class AlarmModeEntity: EmbeddedRealmObject

class PeriodEntity: AlarmModeEntity() {
    var selectedDate: RealmList<Int> = realmListOf()
    var interval: Long = 0L
    var alarmStartTime: String = ""
    var alarmEndTime: String = ""
    var alarm: AlarmEntity = AlarmEntity()
}

class CustomEntity: AlarmModeEntity() {
    var selectedDate: RealmList<Int> = realmListOf()
    var alarmList: RealmList<AlarmEntity> = realmListOf()
}

class AlarmEtcSettingsEntity: EmbeddedRealmObject {
    var stopReachedGoal: Boolean = false
    var delayTomorrow: Boolean = false
}

class AlarmEntity: EmbeddedRealmObject {
    var alarmId: Int = 0
    var startTime: Long = 0L
    var enabled: Boolean = false
}