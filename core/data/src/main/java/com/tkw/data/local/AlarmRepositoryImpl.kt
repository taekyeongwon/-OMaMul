package com.tkw.data.local

import com.tkw.database.AlarmDao
import com.tkw.database.model.AlarmSettingsEntity
import com.tkw.domain.AlarmRepository
import com.tkw.domain.IAlarmManager
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmModeSetting
import com.tkw.domain.model.AlarmSettings
import com.tkw.domain.model.RingTone
import com.tkw.domain.model.RingToneMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
                    update(AlarmSettings())
                } else {
                    emit(AlarmMapper.alarmSettingToModel(settingList))
                }
            }
        }
    }

    override suspend fun update(setting: AlarmSettings) =
        alarmDao.updateSetting(AlarmMapper.alarmSettingToEntity(setting))

    override suspend fun setAlarm(alarmId: Int, startTime: Long, interval: Long) {
        if(alarmId != -1) {
            val alarm = Alarm(alarmId, startTime, true, interval)

            alarmManager.setAlarm(alarm)
            alarmDao.updateAlarm(AlarmMapper.alarmToEntity(alarm))
        }
    }

    override suspend fun updateAlarmModeSetting(setting: AlarmModeSetting) {
        alarmDao.updateAlarmModeSetting(AlarmMapper.alarmModeToEntity(setting))
    }

    override fun getAlarmModeSetting(): Flow<AlarmModeSetting?> =
        alarmDao.getAlarmModeSetting().map {
            runCatching {
                AlarmMapper.alarmModeToModel(it)
            }.getOrNull()
        }

    override suspend fun wakeAllAlarm() {
        //모든 알람 가져와서 실행. 알람 객체는 현재 enable 상태 가지고 있고, enable true 상태인 알람만 전부 다시 켜기.
        val alarmListEntity = alarmDao.getEnabledAlarmModeSetting()

        alarmListEntity?.alarmList?.forEach {
            setAlarm(it.alarmId, it.startTime, it.interval)
        }
    }

    override suspend fun cancelAlarm(alarmId: Int) {
        alarmManager.cancelAlarm(alarmId)
        alarmDao.cancelAlarm(alarmId)
    }

    override suspend fun cancelAllAlarm() {
        //모든 알람 객체 가져와서 enable true인 알람 모두 id값 대로 취소 후 enable false 상태로 업데이트.
        val alarmListEntity = alarmDao.getEnabledAlarmModeSetting()

        alarmListEntity?.alarmList?.forEach {
            cancelAlarm(it.alarmId)
        }
    }

    override suspend fun deleteAlarm(alarmId: Int) =
        alarmDao.deleteAlarm(alarmId)

    override suspend fun allDelete() {
        alarmDao.allDelete()
    }
}