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

    /**
     * 알람을 db에 저장한다. 알람이 켜져 있는 경우 알람 매니저에 등록한다.
     * @param alarm 저장할 알람 객체
     * @param isNotificationEnabled 알람 허용 여부
     * @param isReachedGoal 목표 도달 시 다음으로 미루기 허용 여부
     */
    suspend fun setAlarm(alarm: Alarm, isNotificationEnabled: Boolean = true, isReachedGoal: Boolean = false)

    /**
     * 알람을 리스트로 db에 추가한다. dao에서 clear 후 addAll 함.
     * @param alarmList 저장할 알람 리스트
     * @param isNotificationEnabled 알람 허용 여부
     * @param isReachedGoal 목표 도달 시 다음으로 미루기 허용 여부
     */
    suspend fun setAlarmList(alarmList: List<Alarm>, isNotificationEnabled: Boolean = true, isReachedGoal: Boolean = false)

    /**
     * 순서 변경 등 알람 리스트만 업데이트
     */
    suspend fun updateList(list: List<Alarm>, mode: AlarmMode)

    suspend fun wakeAlarm(alarm: Alarm)

    /**
     * 현재 모드에 해당하는 enabled true인 알람 전부 알람매니저에 등록. 알람 허용여부 체크 후 호출한다.
     */
    suspend fun wakeAllAlarm()

    /**
     * 아직 안울린 오늘의 모든 알람 다음 날로 딜레이. 알람 허용여부 체크와 함께 사용한다.
     * @param isDelayed true인 경우 다음날이 기준이 되어 다음 요일에 알람 설정. false인 경우 오늘 날짜로 알람 설정.
     * @param isNotificationEnabled 알람 허용 여부
     */
    suspend fun delayAllAlarm(isDelayed: Boolean, isNotificationEnabled: Boolean = true)

    suspend fun cancelAlarm(alarmId: String, mode: AlarmMode)

    suspend fun cancelAllAlarm(mode: AlarmMode)

    suspend fun sleepAlarm(alarmId: String)

    suspend fun sleepAllAlarm(mode: AlarmMode)

    suspend fun deleteAlarm(list: List<Alarm>, mode: AlarmMode)

    suspend fun deleteAllAlarm(mode: AlarmMode)
}