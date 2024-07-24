package com.tkw.alarmnoti

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.tkw.domain.AlarmRepository
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.RingToneMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WaterAlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var alarmRepository: AlarmRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent != null) {
            //주기 모드면 interval만큼 startTime에 더해서 실행
            //그 외에는 시간마다 각 알람 설정하고, 24시간만큼 startTime에 더해서 실행
            val alarmId = intent.getIntExtra("ALARM_ID", -1)
            val alarmTime = intent.getLongExtra("ALARM_TIME", -1)
            val alarmInterval = intent.getLongExtra("ALARM_INTERVAL", -1)
            val startTime = alarmTime + alarmInterval

            CoroutineScope(Dispatchers.Main).launch {
                val alarmSettings = alarmRepository.getAlarmSetting().firstOrNull()
                val ringtone = alarmSettings?.ringToneMode ?: RingToneMode()
                NotificationManager.notify(context, ringtone)

                alarmRepository.setAlarm(
                    Alarm(
                        alarmId,
                        startTime,
                        alarmInterval,
                        true    //todo 다음 알람 설정 시 week값 판단해서 설정해야 함.
                    )
                )
            }

            Log.d("test", "alarmnoti received $alarmId")
        }

    }
}