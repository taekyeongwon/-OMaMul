package com.tkw.data.local

import android.util.Log
import com.tkw.database.AlarmDao
import com.tkw.domain.AlarmRepository
import com.tkw.domain.IAlarmManager
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmList
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmModeSetting
import com.tkw.domain.model.AlarmSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.ceil

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

            if(startTime > System.currentTimeMillis()) {    //현재 시간 이후의 알람만 울리도록
                alarmManager.setAlarm(alarm)
                alarmDao.updateAlarm(AlarmMapper.alarmToEntity(alarm))
            } else {
                val x = ceil((System.currentTimeMillis() - startTime).toDouble() / interval).toInt()
                setAlarm(alarmId, startTime + interval * x, interval)
            }
        }
        Log.d("setAlarm", "alarmId : ${alarmId}, startTime : $startTime, ${getDateTimeString(startTime)}")
    }

    override suspend fun updateAlarmModeSetting(setting: AlarmModeSetting) {
        alarmDao.updateAlarmModeSetting(AlarmMapper.alarmModeToEntity(setting))
    }

    override fun getAlarmModeSetting(mode: AlarmMode): Flow<AlarmModeSetting> {
        val alarmModeSetting = alarmDao.getAlarmModeSetting(AlarmMapper.alarmModeToEntity(mode))
        return flow {
            alarmModeSetting.collect {
                if(it == null) {
                    when(mode) {
                        AlarmMode.PERIOD -> updateAlarmModeSetting(AlarmModeSetting.Period())
                        AlarmMode.CUSTOM -> updateAlarmModeSetting(AlarmModeSetting.Custom())
                    }
                } else {
                    emit(AlarmMapper.alarmModeToModel(it))
                }
            }
        }
    }

    override fun getAlarmList(): Flow<AlarmList> {
        return alarmDao.getAlarmList().map {
            AlarmMapper.alarmListToModel(it)
        }
    }

    override suspend fun wakeAllAlarm() {
        //모든 알람 가져와서 실행. 알람 객체는 현재 enable 상태 가지고 있고, enable true 상태인 알람만 전부 다시 켜기.
        val alarmListEntity = alarmDao.getEnabledAlarmList()

        alarmListEntity.alarmList.forEach {
            setAlarm(it.alarmId, it.startTime, it.interval)
        }
    }

    override suspend fun cancelAlarm(alarmId: Int) {
        alarmManager.cancelAlarm(alarmId)
        alarmDao.cancelAlarm(alarmId)
    }

    override suspend fun cancelAllAlarm() {
        //모든 알람 객체 가져와서 enable true인 알람 모두 id값 대로 취소 후 enable false 상태로 업데이트.
        val alarmListEntity = alarmDao.getEnabledAlarmList()

        alarmListEntity.alarmList.forEach {
            cancelAlarm(it.alarmId)
        }
    }

    override suspend fun sleepAlarm() {
        //현재 모드로 활성화 된 알람, 즉 알람매니저에 등록된 알람 cancel 처리. wakeAllAlarm() 호출 시 깨어난다.
        val alarmListEntity = alarmDao.getEnabledAlarmList()

        alarmListEntity.alarmList.forEach {
            alarmManager.cancelAlarm(it.alarmId)
        }
    }

    override suspend fun deleteAlarm(alarmId: Int) {
        alarmManager.cancelAlarm(alarmId)
        alarmDao.deleteAlarm(alarmId)
    }

    override suspend fun allDelete(mode: AlarmMode) {
        cancelAllAlarm()
        alarmDao.allDelete(AlarmMapper.alarmModeToEntity(mode))
    }

    //로그용
    private fun getDateTimeString(timeInMillis: Long): String {
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
        val localDateTime = Instant.ofEpochMilli(timeInMillis)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
        return localDateTime.format(formatter)
    }
}