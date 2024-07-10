package com.tkw.database.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

interface AlarmModeSettingEntity {
    var alarmList: RealmList<AlarmEntity>
}
//Period, CustomEntity interval 값은 알람 설정 화면에서 등록한 값.
//AlarmEntity interval 값은 다음 알람 세팅 시 사용할 값.
class PeriodEntity: RealmObject, AlarmModeSettingEntity {
    @PrimaryKey
    var id: Int = AlarmSettingsEntity.DEFAULT_SETTING_ID
    var selectedDate: RealmList<Int> = realmListOf()
    var interval: Long = -1L
    var alarmStartTime: String = ""
    var alarmEndTime: String = ""
    override var alarmList: RealmList<AlarmEntity> = realmListOf()
}

class CustomEntity: RealmObject, AlarmModeSettingEntity {
    @PrimaryKey
    var id: Int = AlarmSettingsEntity.DEFAULT_SETTING_ID
    var selectedDate: RealmList<Int> = realmListOf()
    var interval: Long = -1L
    override var alarmList: RealmList<AlarmEntity> = realmListOf()
}