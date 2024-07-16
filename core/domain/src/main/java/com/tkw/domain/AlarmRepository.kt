package com.tkw.domain

import com.tkw.domain.model.AlarmModeSetting
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmSettings
import kotlinx.coroutines.flow.Flow

interface AlarmRepository {
    fun getAlarmSetting(): Flow<AlarmSettings>

    suspend fun update(setting: AlarmSettings)

    suspend fun setAlarm(alarmId: Int, startTime: Long, interval: Long)

    suspend fun updateAlarmModeSetting(setting: AlarmModeSetting)

    fun getAlarmModeSetting(mode: AlarmMode): Flow<AlarmModeSetting?>

    suspend fun wakeAllAlarm()

    suspend fun cancelAlarm(alarmId: Int)

    suspend fun cancelAllAlarm()

    suspend fun deleteAlarm(alarmId: Int)

    suspend fun allDelete()
}