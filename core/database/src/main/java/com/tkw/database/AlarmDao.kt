package com.tkw.database

import com.tkw.database.model.AlarmSettingsEntity

interface AlarmDao: RealmDao<AlarmSettingsEntity> {
    fun updateSetting(setting: AlarmSettingsEntity)

    fun getSetting(): AlarmSettingsEntity

    fun updateAlarm(/*alarm: AlarmEntity*/)

    fun deleteAlarm(alarmId: Int)
}