package com.tkw.domain

import com.tkw.domain.model.AlarmSettings

interface AlarmRepository {
    fun update(setting: AlarmSettings)

    fun wakeAllAlarm()

    fun setAlarm(startTime: Long, alarmId: Int)

    fun getAlarm(alarmId: Int)//: Alarm 객체 반환

    fun getAllAlarm()//: List<Alarm>

    fun cancelAlarm()

    fun cancelAllAlarm()
}