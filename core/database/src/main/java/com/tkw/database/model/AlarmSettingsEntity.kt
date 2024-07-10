package com.tkw.database.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class AlarmSettingsEntity: RealmObject {
    @PrimaryKey
    var id: Int = DEFAULT_SETTING_ID
    private var ringToneMode: String = RingToneEntity.BELL.state
    var ringToneEnum: RingToneEntity
        get() = RingToneEntity.valueOf(ringToneMode)
        set(value) {
            ringToneMode = value.state
        }
    var alarmList: AlarmListEntity? = null
    var etcSetting: AlarmEtcSettingsEntity? = null

    companion object {
        const val DEFAULT_SETTING_ID = 0    //세팅은 하나만 존재하므로 0 고정
    }
}

enum class RingToneEntity(var state: String) {
    BELL("BELL"), VIBE("VIBE"), ALL("ALL"), IGNORE("IGNORE")
}

class AlarmListEntity: EmbeddedRealmObject {
    private var alarmMode: String = AlarmModeEnum.PERIOD.state
    var alarmModeEnum: AlarmModeEnum
        get() = AlarmModeEnum.valueOf(alarmMode)
        set(value) {
            alarmMode = value.state
        }
    var period: PeriodEntity? = null
    var custom: CustomEntity? = null
}

enum class AlarmModeEnum(var state: String) {
    PERIOD("PERIOD"), CUSTOM("CUSTOM")
}

//Period, CustomEntity interval 값은 알람 설정 화면에서 등록한 값.
//AlarmEntity interval 값은 다음 알람 세팅 시 사용할 값.
class PeriodEntity: EmbeddedRealmObject {
    var selectedDate: RealmList<Int> = realmListOf()
    var interval: Long = -1L
    var alarmStartTime: String = ""
    var alarmEndTime: String = ""
    var alarmList: RealmList<AlarmEntity> = realmListOf()
}

class CustomEntity: EmbeddedRealmObject {
    var selectedDate: RealmList<Int> = realmListOf()
    var interval: Long = -1L
    var alarmList: RealmList<AlarmEntity> = realmListOf()
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