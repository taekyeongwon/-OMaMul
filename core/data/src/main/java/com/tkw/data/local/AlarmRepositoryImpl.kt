package com.tkw.data.local

import com.tkw.database.AlarmDao
import com.tkw.database.model.AlarmModeEnum
import com.tkw.database.model.AlarmSettingsEntity
import com.tkw.domain.AlarmRepository
import com.tkw.domain.IAlarmManager
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmSettings
import com.tkw.domain.model.RingTone
import io.realm.kotlin.notifications.InitialResults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
    private val alarmManager: IAlarmManager
): AlarmRepository {
    override fun getAlarmSetting(): Flow<AlarmSettings?> =
        alarmDao.getSetting().map {
            runCatching {
                AlarmMapper.alarmSettingToModel(it.list.first())
            }.getOrNull()
        }

    override suspend fun update(setting: AlarmSettings) =
        alarmDao.updateSetting(AlarmMapper.alarmSettingToEntity(setting))

    override suspend fun setAlarm(alarmId: Int, startTime: Long, interval: Long) {
        if(alarmId != -1) {
            val alarm = Alarm(alarmId, startTime, true, interval)
//            val ringtone = alarmDao.getRingtone()


            alarmManager.setAlarm(alarm, RingTone.ALL)
            alarmDao.updateAlarm(AlarmMapper.alarmToEntity(alarm))
        }
    }

    override fun getAlarmList(): List<Alarm> {
        val alarmList = ArrayList<Alarm>()

        alarmDao.getAlarmList()
            .forEach {
                alarmList.add(AlarmMapper.alarmToModel(it))
            }
        return alarmList
    }

    override suspend fun wakeAllAlarm() {
        //모든 알람 가져와서 실행. 알람 객체는 현재 enable 상태 가지고 있고, enable true 상태인 알람만 전부 다시 켜기.
        val alarmListEntity = alarmDao.getEnabledAlarmList()

        alarmListEntity.forEach {
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

        alarmListEntity.forEach {
            cancelAlarm(it.alarmId)
        }
    }

    override suspend fun deleteAlarm(alarmId: Int) =
        alarmDao.deleteAlarm(alarmId)
}