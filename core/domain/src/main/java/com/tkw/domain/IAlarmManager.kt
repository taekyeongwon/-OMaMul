package com.tkw.domain

import com.tkw.domain.model.Alarm

interface IAlarmManager {
    fun canScheduleExactAlarms(): Boolean
    fun setAlarm(alarm: Alarm)
    fun cancelAlarm(alarmId: Int)
}