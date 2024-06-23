package com.tkw.omamul

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.viewmodel.viewModelFactory
import com.tkw.common.NotificationManager
import com.tkw.common.WaterAlarmManager
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

    override fun onReceive(context: Context, intent: Intent) {
        CoroutineScope(Dispatchers.Main).launch {
            if(intent.action == Intent.ACTION_BOOT_COMPLETED) {
                if (
                    NotificationManager.isNotificationEnabled(context)
                    && prefRepository.fetchAlarmEnableFlag().first() == true
                ) {
                    WaterAlarmManager.setAlarm(context, 0, 0)
                }
            }
        }

    }
}