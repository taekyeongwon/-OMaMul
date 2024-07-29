package com.tkw.domain

import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmList
import com.tkw.domain.model.AlarmModeSetting
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmSettings
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAlarmSetting(): Flow<AlarmSettings>

    suspend fun updateAlarmSetting(setting: AlarmSettings)

    fun getAlarmModeSetting(mode: AlarmMode): Flow<AlarmModeSetting>

    suspend fun updateAlarmModeSetting(setting: AlarmModeSetting)

    fun getAlarmList(mode: AlarmMode): Flow<AlarmList>

    suspend fun setAlarm(alarm: Alarm)

    /**
     * 알람 리스트 추가 및 알람매니저 등록
     */
    suspend fun setAlarmList(alarmList: List<Alarm>)

    /**
     * 순서 변경 등 알람 리스트만 업데이트
     */
    suspend fun updateList(list: List<Alarm>, mode: AlarmMode)

    suspend fun wakeAlarm(alarm: Alarm)

    suspend fun wakeAllAlarm()

    suspend fun cancelAlarm(alarmId: Int, mode: AlarmMode)

    suspend fun cancelAllAlarm(mode: AlarmMode)

    suspend fun sleepAlarm(alarmId: Int)

    suspend fun sleepAllAlarm(mode: AlarmMode)

    suspend fun deleteAlarm(alarmId: Int, mode: AlarmMode)

    suspend fun deleteAllAlarm(mode: AlarmMode)
}