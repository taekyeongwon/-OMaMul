package com.tkw.omamul

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
class BootReceiver: BroadcastReceiver() {

    @Inject
    lateinit var prefRepository: PrefDataRepository

    @Inject
    lateinit var alarmRepository: AlarmRepository

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.Main).launch {
            if(intent.action == Intent.ACTION_BOOT_COMPLETED) {
                if (
                    NotificationManager.isNotificationEnabled(context)
                    && prefRepository.fetchAlarmEnableFlag().first() == true
                ) {
                    alarmRepository.wakeAllAlarm()
//                    WaterAlarmManager.setAlarm(context, 0, 0)
                }
            }
        }

    }
}