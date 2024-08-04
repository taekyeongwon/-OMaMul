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

    /**
     * 현재 선택된 모드에서 가장 남은시간이 가까운 알람 가져오기
     */
    fun getRemainAlarmTime(): Flow<Long>

    suspend fun setAlarm(alarm: Alarm, isNotificationEnabled: Boolean = true, isReachedGoal: Boolean = false)

    /**
     * 알람 리스트 추가 및 알람매니저 등록
     */
    suspend fun setAlarmList(alarmList: List<Alarm>, isNotificationEnabled: Boolean = true, isReachedGoal: Boolean = false)

    /**
     * 순서 변경 등 알람 리스트만 업데이트
     */
    suspend fun updateList(list: List<Alarm>, mode: AlarmMode)

    suspend fun wakeAlarm(alarm: Alarm)

    /**
     * 현재 모드에 해당하는 enabled true인 알람 전부 알람매니저에 등록. 알람 허용여부 체크와 함께 사용한다.
     */
    suspend fun wakeAllAlarm()

    /**
     * 아직 안울린 오늘의 모든 알람 다음 날로 딜레이. 알람 허용여부 체크와 함께 사용한다.
     */
    suspend fun delayAllAlarm(isDelayed: Boolean, isNotificationEnabled: Boolean = true)

    suspend fun cancelAlarm(alarmId: String, mode: AlarmMode)

    suspend fun cancelAllAlarm(mode: AlarmMode)

    suspend fun sleepAlarm(alarmId: String)

    suspend fun sleepAllAlarm(mode: AlarmMode)

    suspend fun deleteAlarm(list: List<Alarm>, mode: AlarmMode)

    suspend fun deleteAllAlarm(mode: AlarmMode)
}