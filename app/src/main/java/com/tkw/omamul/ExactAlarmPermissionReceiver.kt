package com.tkw.omamul

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tkw.common.WaterAlarmManager

class ExactAlarmPermissionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                // onReceive
                WaterAlarmManager.setAlarm(context, 0, 0)
            }
        }
    }
}