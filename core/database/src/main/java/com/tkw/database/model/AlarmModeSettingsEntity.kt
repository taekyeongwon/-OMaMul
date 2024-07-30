package com.tkw.database.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter


//Period, CustomEntity interval 값은 알람 설정 화면에서 등록한 값.
//AlarmEntity interval 값은 다음 알람 세팅 시 사용할 값.
class AlarmModeSettingEntity: RealmObject {
    @PrimaryKey
    var id: Int = AlarmSettingsEntity.DEFAULT_SETTING_ID
    var selectedDate: RealmList<Int> = realmListOf()
    var interval: Int = -1
}

class AlarmEntity: EmbeddedRealmObject {
    var alarmId: String = ""
    var startTime: Long = -1L
//    var interval: Long = -1L
    var selectedDates: RealmList<Int> = realmListOf()
    var enabled: Boolean = false

    override fun toString(): String {
        return "{" +
                "\"alarmId\": $alarmId, " +
                "\"startTime\": $startTime, " +
                "\"startTime\": ${getDateTimeString(startTime)}, " +
                "\"selectedDates\": ${selectedDates.joinToString(", ")}, " +
                "\"enabled\": $enabled" +
                "}"
    }

    override fun equals(other: Any?): Boolean {
        val otherEntity = other as? AlarmEntity
        return (alarmId == otherEntity?.alarmId
                && startTime == otherEntity.startTime
                && selectedDates == otherEntity.selectedDates
                && enabled == otherEntity.enabled
                )
    }

    //로그용
    private fun getDateTimeString(timeInMillis: Long): String {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val localDateTime = Instant.ofEpochMilli(timeInMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        return localDateTime.format(formatter)
    }

    override fun hashCode(): Int {
        var result = alarmId.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + selectedDates.hashCode()
        result = 31 * result + enabled.hashCode()
        return result
    }
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