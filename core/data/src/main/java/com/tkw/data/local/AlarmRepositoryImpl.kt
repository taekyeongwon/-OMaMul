package com.tkw.data.local

import com.tkw.database.AlarmDao
import com.tkw.domain.AlarmRepository
import com.tkw.domain.IAlarmManager
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmList
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmModeSetting
import com.tkw.domain.model.AlarmSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import java.time.LocalDate
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
    private val alarmManager: IAlarmManager
): AlarmRepository {

    override fun getAlarmSetting(): Flow<AlarmSettings> {
        val setting = alarmDao.getSetting()
        return flow {
            setting.collect {
                val settingList = it.list.firstOrNull()
                if(settingList == null) {
                    updateAlarmSetting(AlarmSettings())
                } else {
                    emit(AlarmMapper.alarmSettingToModel(settingList))
                }
            }
        }
    }

    override suspend fun updateAlarmSetting(setting: AlarmSettings) =
        alarmDao.updateSetting(AlarmMapper.alarmSettingToEntity(setting))

    override fun getAlarmModeSetting(mode: AlarmMode): Flow<AlarmModeSetting> {
        val alarmModeSetting = alarmDao.getAlarmModeSetting(AlarmMapper.alarmModeToEntity(mode))
        return flow {
            alarmModeSetting.collect {
                if(it == null) {
                    updateAlarmModeSetting(AlarmModeSetting())
                } else {
                    emit(AlarmMapper.alarmModeToModel(it))
                }
            }
        }
    }

    override suspend fun updateAlarmModeSetting(setting: AlarmModeSetting) {
        alarmDao.updateAlarmModeSetting(AlarmMapper.alarmModeToEntity(setting))
    }

    override fun getAlarmList(mode: AlarmMode): Flow<AlarmList> {
        return alarmDao.getAlarmList(AlarmMapper.alarmModeToEntity(mode)).map {
            AlarmMapper.alarmListToModel(it)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getRemainAlarmTime(): Flow<Long> =
        getAlarmSetting().mapLatest {
            it.alarmMode
        }.flatMapLatest {
            getAlarmList(it)
        }.flatMapLatest { result ->
            flow {
                if (result.alarmList.none { it.enabled }) {
                    emit(-1L)
                } else {
                    val closestAlarm = result.alarmList
                        .filter { it.enabled }
                        .minOf {
                            it.startTime - System.currentTimeMillis()
                        }
                    emit(closestAlarm)
                }
            }
        }

    override suspend fun setAlarm(alarm: Alarm, isNotificationEnabled: Boolean, isReachedGoal: Boolean) {
        with(alarm) {
            val currentMode = getAlarmSetting().first().alarmMode
            val calculatedInterval = if(isReachedGoal) {
                val nextDate = LocalDate.now().plusDays(1)
                alarm.copy(startTime = alarm.setLocalDateTimeTo(nextDate))
                    .getIntervalByNextDayOfWeek()
            } else {
                alarm.getIntervalByNextDayOfWeek()
            }

            val newAlarm = if(calculatedInterval == -1) {   //선택된 요일이 없는 경우 enabled false로 저장
                alarm.copy(enabled = false)
            } else {    //선택한 요일이 있으면 해당 요일 까지 지연
                alarm.copy(startTime = startTime + calculatedInterval)
            }
            alarmDao.setAlarm(
                AlarmMapper.alarmToEntity(newAlarm),
                AlarmMapper.alarmModeToEntity(currentMode)
            )
            if(newAlarm.enabled && isNotificationEnabled) {
                wakeAlarm(newAlarm)
            } else {
                sleepAlarm(newAlarm.alarmId)
            }
        }
    }

    override suspend fun setAlarmList(alarmList: List<Alarm>, isNotificationEnabled: Boolean, isReachedGoal: Boolean) {
        val currentMode = getAlarmSetting().first().alarmMode
        deleteAllAlarm(currentMode)

        val newList = alarmList.map {
            val calculatedInterval = if(isReachedGoal) {
                val nextDate = LocalDate.now().plusDays(1)
                it.copy(startTime = it.setLocalDateTimeTo(nextDate))
                    .getIntervalByNextDayOfWeek()
            } else {
                it.getIntervalByNextDayOfWeek()
            }
            if(calculatedInterval == -1) {
                it.copy(enabled = false)
            } else {
                it.copy(startTime = it.startTime + calculatedInterval)
            }
        }
        alarmDao.setAlarmList(
            AlarmMapper.alarmListToEntity(newList),
            AlarmMapper.alarmModeToEntity(currentMode)
        )
        newList.forEach {
            if(it.enabled && isNotificationEnabled) {
                wakeAlarm(it)
            } else {
                sleepAlarm(it.alarmId)
            }
        }
    }

    override suspend fun updateList(list: List<Alarm>, mode: AlarmMode) {
        val mappedList = list.map {
            AlarmMapper.alarmToEntity(it)
        }
        alarmDao.setAlarmList(mappedList, AlarmMapper.alarmModeToEntity(mode))
    }

    override suspend fun wakeAlarm(alarm: Alarm) {
        if(alarm.startTime < System.currentTimeMillis()) {
            val currentDate = LocalDate.now()
            setAlarm(alarm.copy(startTime = alarm.setLocalDateTimeTo(currentDate)))
        } else {
            alarmManager.setAlarm(alarm)
        }
    }

    override suspend fun wakeAllAlarm() {
        //모든 알람 가져와서 실행. 알람 객체는 현재 enable 상태 가지고 있고, enable true 상태인 알람만 전부 다시 켜기.
        val currentMode = getAlarmSetting().first().alarmMode
        val alarmListEntity = alarmDao.getEnabledAlarmList(
            AlarmMapper.alarmModeToEntity(currentMode)
        )

        alarmListEntity.alarmList.forEach {
            wakeAlarm(AlarmMapper.alarmToModel(it))
        }
    }

    override suspend fun delayAllAlarm(isDelayed: Boolean, isNotificationEnabled: Boolean) {
        //만약 reached면 전체 알람 다 가져와서(enabled 상관 없이) startTime date를 내일로 설정.
        //reached가 아니면 startTime date 오늘로 설정
        //이후 전부 setAlarm호출
        val currentMode = getAlarmSetting().first().alarmMode
        val mappedList = getAlarmList(currentMode).map {
            it.alarmList.map {
                val nextDate = if(isDelayed) LocalDate.now().plusDays(1)
                else LocalDate.now()
                it.copy(startTime = it.setLocalDateTimeTo(nextDate))
            }
        }.first()
        setAlarmList(mappedList, isNotificationEnabled)
    }

    override suspend fun cancelAlarm(alarmId: String, mode: AlarmMode) {
        sleepAlarm(alarmId)
        alarmDao.cancelAlarm(alarmId, AlarmMapper.alarmModeToEntity(mode))
    }

    override suspend fun cancelAllAlarm(mode: AlarmMode) {
        //모든 알람 객체 가져와서 enable true인 알람 모두 id값 대로 취소 후 enable false 상태로 업데이트.
        val alarmListEntity = alarmDao.getEnabledAlarmList(
            AlarmMapper.alarmModeToEntity(mode)
        )

        alarmListEntity.alarmList.forEach {
            cancelAlarm(it.alarmId, mode)
        }
    }

    override suspend fun sleepAlarm(alarmId: String) {
        alarmManager.cancelAlarm(alarmId)
    }

    override suspend fun sleepAllAlarm(mode: AlarmMode) {
        //현재 모드로 활성화 된 알람, 즉 알람매니저에 등록된 알람 cancel 처리. wakeAllAlarm() 호출 시 깨어난다.
        val alarmListEntity = alarmDao.getEnabledAlarmList(
            AlarmMapper.alarmModeToEntity(mode)
        )

        alarmListEntity.alarmList.forEach {
            sleepAlarm(it.alarmId)
        }
    }

    override suspend fun deleteAlarm(alarmId: String, mode: AlarmMode) {
        sleepAlarm(alarmId)
        alarmDao.deleteAlarm(alarmId, AlarmMapper.alarmModeToEntity(mode))
    }

    override suspend fun deleteAllAlarm(mode: AlarmMode) {
        sleepAllAlarm(mode)
        alarmDao.deleteAllAlarm(AlarmMapper.alarmModeToEntity(mode))
    }
}