package com.tkw.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WaterAlarmManager {

    //todo ringtone mode 받아서 extra로 넘겨줌.
    fun setAlarm(context: Context, startTime: Long, interval: Long) {
        if(canScheduleExactAlarms(context)) {
            setAlarmManager(context)
            cancelWorkManager(context)
        } else {
            setWorkManager(context)
            cancelAlarmManager(context)
        }
    }

    fun cancelAlarm(context: Context) {
        cancelAlarmManager(context)
        cancelWorkManager(context)
    }

    private fun canScheduleExactAlarms(context: Context): Boolean {
        return if(Build.VERSION.SDK_INT >= 31) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else true
    }

    private fun setAlarmManager(context: Context) {
        val intent = Intent(context, WaterAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmClock = AlarmManager.AlarmClockInfo(
            Calendar.getInstance().timeInMillis + 1000 * 60,
            pendingIntent
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setAlarmClock(
            alarmClock, pendingIntent
        )
    }

    private fun setWorkManager(context: Context) {
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<ScheduledWorkManager>()
            .setInitialDelay(ScheduledWorkManager.getCertainTime(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            ScheduledWorkManager.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            oneTimeWorkRequest
        )
    }

    private fun cancelAlarmManager(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, WaterAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun cancelWorkManager(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(ScheduledWorkManager.WORK_NAME)
    }
}