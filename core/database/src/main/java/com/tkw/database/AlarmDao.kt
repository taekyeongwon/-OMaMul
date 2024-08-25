package com.tkw.database

import com.tkw.database.model.AlarmEntity
import com.tkw.database.model.AlarmListEntity
import com.tkw.database.model.AlarmModeEntity
import com.tkw.database.model.AlarmModeSettingEntity
import com.tkw.database.model.AlarmSettingsEntity
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface AlarmDao: RealmDao {
    /**
     * 알람 설정 객체 요청
     */
    fun getSetting(): Flow<ResultsChange<AlarmSettingsEntity>>

    /**
     * 알람 설정 객체 업데이트
     */
    suspend fun updateSetting(setting: AlarmSettingsEntity)

    /**
     * 현재 설정된 모드에 해당하는
     * PeriodEntity/CustomEntity 객체 리턴
     */
    fun getAlarmModeSetting(): Flow<AlarmModeSettingEntity?>

    /**
     * Period/Custom 설정 변경 시 업데이트.
     * alarmList의 경우 setAlarm을 통해서만 업데이트 된다.
     */
    suspend fun updateAlarmModeSetting(alarmModeSettingEntity: AlarmModeSettingEntity)

    /**
     * alarm enable 여부에 상관 없이 Period/CustomAlarmListEntity 객체 리턴
     */
    fun getAlarmList(mode: AlarmModeEntity): Flow<AlarmListEntity>

    /**
     * Period/CustomAlarmListEntity 객체 내 alarm.enabled == true 필터링 된 알람 리스트 리턴
     */
    suspend fun getEnabledAlarmList(alarmMode: AlarmModeEntity): AlarmListEntity

    /**
     * 알람 id가 존재하면 update, 없으면 insert
     */
    suspend fun setAlarm(alarm: AlarmEntity, alarmMode: AlarmModeEntity)

    /**
     * 알람 리스트 업데이트
     */
    suspend fun setAlarmList(list: List<AlarmEntity>, mode: AlarmModeEntity)

    /**
     * alarmId에 해당하는 Alarm 객체의 enabled를 false로 변경
     */
    suspend fun cancelAlarm(alarmId: String, mode: AlarmModeEntity)

    /**
     * alarmId에 해당하는 Alarm 객체 제거
     */
    suspend fun deleteAlarm(list: List<AlarmEntity>, mode: AlarmModeEntity)

    /**
     * 전체 알람 제거 (Period 알람 간격 재 설정 시 호출)
     */
    suspend fun deleteAllAlarm(mode: AlarmModeEntity)
}