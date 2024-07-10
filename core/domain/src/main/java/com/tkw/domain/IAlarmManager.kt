package com.tkw.domain

import com.tkw.domain.model.Alarm
import com.tkw.domain.model.RingTone

interface IAlarmManager {
    fun canScheduleExactAlarms(): Boolean
    fun setAlarm(alarm: Alarm, ringTone: RingTone)
    fun cancelAlarm(alarmId: Int)
}