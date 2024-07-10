package com.tkw.database

import com.tkw.database.model.AlarmEntity
import com.tkw.database.model.AlarmListEntity
import com.tkw.database.model.AlarmSettingsEntity
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface AlarmDao: RealmDao<AlarmSettingsEntity> {
    /**
     * 알람 설정 객체 업데이트
     */
    suspend fun updateSetting(setting: AlarmSettingsEntity)

    /**
     * 알람 설정 객체 요청
     */
    fun getSetting(): Flow<ResultsChange<AlarmSettingsEntity>>

    /**
     * 알람 id가 존재하면 update, 없으면 insert
     */
    suspend fun updateAlarm(alarm: AlarmEntity)

    /**
     * alarmId에 해당하는 Alarm 객체의 enabled를 false로 변경
     */
    suspend fun cancelAlarm(alarmId: Int)

    /**
     * AlarmListEntity에서 현재 설정된 모드에 해당하는
     * PeriodEntity/CustomEntity 객체 내 alarmList만 리턴
     */
    fun getAlarmList(): List<AlarmEntity>

    /**
     * AlarmListEntity에서 현재 설정된 모드에 해당하는
     * PeriodEntity/CustomEntity 객체 내 alarm.enabled == true 인 알람 리스트만 리턴
     */
    fun getEnabledAlarmList(): List<AlarmEntity>

    /**
     * alarmId에 해당하는 Alarm 객체 제거
     */
    suspend fun deleteAlarm(alarmId: Int)
}