package com.tkw.alarmnoti

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.tkw.domain.IAlarmManager
import com.tkw.domain.model.Alarm
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WaterAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context
): IAlarmManager {

    override fun setAlarm(alarm: Alarm) {
        if(canScheduleExactAlarms()) {
            Log.d("AlarmManager", "setExactAlarm : $alarm")
            setAlarmManager(alarm)
        } else {
            Log.d("AlarmManager", "setInexactAlarm : $alarm")
            setInexactAlarm(alarm)
        }
    }

    override fun canScheduleExactAlarms(): Boolean {
        return if(Build.VERSION.SDK_INT >= 31) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else true
    }

    private fun setAlarmManager(alarm: Alarm) {
        val pendingIntent = getAlarmPendingIntent(alarm)

        val alarmClock = AlarmManager.AlarmClockInfo(
            alarm.startTime,
            pendingIntent
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAlarmClock(
            alarmClock, pendingIntent
        )
    }

    private fun setInexactAlarm(alarm: Alarm) {
        val pendingIntent = getAlarmPendingIntent(alarm)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            alarm.startTime,
            pendingIntent
        )
    }

    private fun getAlarmPendingIntent(alarm: Alarm): PendingIntent {
        val intent = Intent(context, WaterAlarmReceiver::class.java)
        intent.putExtra("ALARM", alarm)
        return PendingIntent.getBroadcast(
            context,
            alarm.alarmId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun cancelAlarm(alarmId: String) {
        cancelAlarmManager(alarmId)
    }

    private fun cancelAlarmManager(alarmId: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WaterAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}