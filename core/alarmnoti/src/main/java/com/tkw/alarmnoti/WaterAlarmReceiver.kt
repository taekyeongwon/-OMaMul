package com.tkw.alarmnoti

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.tkw.domain.AlarmRepository
import com.tkw.domain.PrefDataRepository
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.RingToneMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WaterAlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var prefRepository: PrefDataRepository

    @Inject
    lateinit var alarmRepository: AlarmRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            //주기 모드면 interval만큼 startTime에 더해서 실행
            //그 외에는 시간마다 각 알람 설정하고, 24시간만큼 startTime에 더해서 실행

            val alarm = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("ALARM", Alarm::class.java)
            } else {
                intent.getSerializableExtra("ALARM") as Alarm
            }
//            val alarmId = intent.getIntExtra("ALARM_ID", -1)
//            val alarmTime = intent.getLongExtra("ALARM_TIME", -1)
//            val alarmInterval = intent.getLongExtra("ALARM_INTERVAL", -1)
//            val startTime = alarmTime + alarmInterval

            CoroutineScope(Dispatchers.Main).launch {
                val alarmSettings = alarmRepository.getAlarmSetting().firstOrNull()
                val ringtone = alarmSettings?.ringToneMode ?: RingToneMode()
                val isNotificationEnabled =
                    NotificationManager.isNotificationEnabled(context)
                            && prefRepository.fetchAlarmEnableFlag().first()

                if(isNotificationEnabled) {
                    NotificationManager.notify(context, ringtone)
                }

                if(alarm != null) {
                    alarmRepository.setAlarm(alarm, isNotificationEnabled)
                    Log.d("AlarmReceiver", "Notification received. alarmId : ${alarm.alarmId}")
                }
            }
        }

    }
}