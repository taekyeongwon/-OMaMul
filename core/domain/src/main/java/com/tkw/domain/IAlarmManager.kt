package com.tkw.domain

interface IAlarmManager {
    fun canScheduleExactAlarms(): Boolean
    fun setAlarm(startTime: Long, interval: Int, alarmId: Int)
    fun cancelAlarm(alarmId: Int)
}