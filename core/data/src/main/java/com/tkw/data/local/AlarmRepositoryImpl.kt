package com.tkw.data.local

import com.tkw.database.AlarmDao
import com.tkw.domain.AlarmRepository
import com.tkw.domain.IAlarmManager
import com.tkw.domain.model.AlarmSettings
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(
    private val alarmDao: AlarmDao,
    private val alarmManager: IAlarmManager
): AlarmRepository {
    override fun update(setting: AlarmSettings) {

    }

    override fun setAlarm() {
        alarmManager.setAlarm(0, 0)
    }

    override fun cancelAlarm() {
        alarmManager.cancelAlarm()
    }
}