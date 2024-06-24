package com.tkw.alarmnoti

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.tkw.domain.IAlarmManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WaterAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context
): IAlarmManager {

    //todo ringtone mode 받아서 extra로 넘겨줌.
    override fun setAlarm(startTime: Long, interval: Long) {
        if(canScheduleExactAlarms()) {
            setAlarmManager()
            cancelWorkManager()
        } else {
            setWorkManager()
            cancelAlarmManager()
        }
    }

    override fun cancelAlarm() {
        cancelAlarmManager()
        cancelWorkManager()
    }

    override fun canScheduleExactAlarms(): Boolean {
        return if(Build.VERSION.SDK_INT >= 31) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.canScheduleExactAlarms()
        } else true
    }

    private fun setAlarmManager() {
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

    private fun setWorkManager() {
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<ScheduledWorkManager>()
            .setInitialDelay(ScheduledWorkManager.getCertainTime(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            ScheduledWorkManager.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            oneTimeWorkRequest
        )
    }

    private fun cancelAlarmManager() {
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

    private fun cancelWorkManager() {
        WorkManager.getInstance(context).cancelUniqueWork(ScheduledWorkManager.WORK_NAME)
    }
}