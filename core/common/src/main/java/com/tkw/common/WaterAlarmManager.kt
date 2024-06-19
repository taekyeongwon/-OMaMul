package com.tkw.common

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import java.util.Calendar

object WaterAlarmManager {

    //todo ringtone mode 받아서 extra로 넘겨줌.
    fun setAlarm(context: Context, startTime: Long, interval: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if(Build.VERSION.SDK_INT >= 31) {
            val hasPermission = alarmManager.canScheduleExactAlarms()
            if(!hasPermission) {
                //todo 설정 이동 팝업 띄우고 취소하는 경우 동작 안하도록, 또는 WorkManager로 변경 및 테스트
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                context.startActivity(intent)
                return
            }
        }

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
        alarmManager.setAlarmClock(
            alarmClock, pendingIntent
        )
    }

    fun cancelAlarm(context: Context) {
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
}