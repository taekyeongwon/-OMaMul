package com.tkw.domain

import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmList
import com.tkw.domain.model.AlarmModeSetting
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmSettings
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAlarmSetting(): Flow<AlarmSettings>

    suspend fun update(setting: AlarmSettings)

    suspend fun setAlarm(alarm: Alarm)

    suspend fun updateAlarmModeSetting(setting: AlarmModeSetting)

    fun getAlarmModeSetting(mode: AlarmMode): Flow<AlarmModeSetting>

    fun getAlarmList(mode: AlarmMode): Flow<AlarmList>

    suspend fun wakeAllAlarm()

    suspend fun cancelAlarm(alarmId: Int, mode: AlarmMode)

    suspend fun cancelAllAlarm(mode: AlarmMode)

    suspend fun sleepAlarm(mode: AlarmMode)

    suspend fun deleteAlarm(alarmId: Int, mode: AlarmMode)

    suspend fun allDelete(mode: AlarmMode)

    suspend fun updateList(list: List<Alarm>, mode: AlarmMode)
}