package com.tkw.database.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey


//Period, CustomEntity interval 값은 알람 설정 화면에서 등록한 값.
//AlarmEntity interval 값은 다음 알람 세팅 시 사용할 값.
class AlarmModeSettingEntity: RealmObject {
    @PrimaryKey
    var id: Int = AlarmSettingsEntity.DEFAULT_SETTING_ID
    var selectedDate: RealmList<Int> = realmListOf()
    var interval: Long = -1L
}

class AlarmEntity: EmbeddedRealmObject {
    var alarmId: Int = -1
    var startTime: Long = -1L
    var interval: Long = -1L
    var selectedDates: RealmList<Int> = realmListOf()
    var enabled: Boolean = false
}

interface AlarmListEntity {
    var alarmList: RealmList<AlarmEntity>
}

class PeriodAlarmListEntity: RealmObject, AlarmListEntity {
    @PrimaryKey
    var id: Int = AlarmSettingsEntity.DEFAULT_SETTING_ID
    override var alarmList: RealmList<AlarmEntity> = realmListOf()
}

class CustomAlarmListEntity: RealmObject, AlarmListEntity {
    @PrimaryKey
    var id: Int = AlarmSettingsEntity.DEFAULT_SETTING_ID
    override var alarmList: RealmList<AlarmEntity> = realmListOf()
}