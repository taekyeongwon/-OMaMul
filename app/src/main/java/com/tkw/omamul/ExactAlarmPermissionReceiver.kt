package com.tkw.omamul

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.tkw.alarmnoti.NotificationManager
import com.tkw.domain.AlarmRepository
import com.tkw.domain.PrefDataRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ExactAlarmPermissionReceiver: BroadcastReceiver() {

    @Inject
    lateinit var prefRepository: PrefDataRepository

    @Inject
    lateinit var alarmRepository: AlarmRepository

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
                // onReceive
                CoroutineScope(Dispatchers.Main).launch {
                    if (
                        NotificationManager.isNotificationEnabled(context)
                        && prefRepository.fetchAlarmEnableFlag().first()
                    ) {
                        alarmRepository.wakeAllAlarm()
                    }
                }
            }
        }
    }
}