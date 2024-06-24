package com.tkw.domain

interface IAlarmManager {
    fun canScheduleExactAlarms(): Boolean
    fun setAlarm(startTime: Long, interval: Long)
    fun cancelAlarm()
}