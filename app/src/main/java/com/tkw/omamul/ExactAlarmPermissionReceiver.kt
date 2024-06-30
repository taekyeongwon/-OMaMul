package com.tkw.omamul

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tkw.domain.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExactAlarmPermissionReceiver: BroadcastReceiver() {
    @Inject
    lateinit var alarmRepository: AlarmRepository

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                // onReceive
                alarmRepository.wakeAllAlarm()
//                WaterAlarmManager.setAlarm(context, 0, 0)
            }
        }
    }
}