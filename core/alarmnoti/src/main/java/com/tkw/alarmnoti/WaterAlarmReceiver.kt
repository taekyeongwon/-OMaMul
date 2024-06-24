package com.tkw.alarmnoti

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import com.tkw.domain.AlarmRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WaterAlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var alarmRepository: AlarmRepository
    override fun onReceive(context: Context?, intent: Intent?) {
        //todo extra로 받아서 buildNotification에 ringtone mode 넘겨줌
        if (context != null) {
            NotificationManager.notify(context)
            alarmRepository.setAlarm()
//            WaterAlarmManager.setAlarm(context, 0, 0)
        }

        Log.d("test", "alarmnoti received")

    }

    /**
     * ringtone mode
     * 핸드폰 설정과 동일한 경우 NOTI_CH 리턴
     * 이외 MUTE_CH 리턴
     */
    private fun getChannel(ringtoneMode: String): String {
        return NotificationManager.MUTE_CH
    }

    private fun playRingtone(context: Context) {
        val uriRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone = RingtoneManager.getRingtone(context, uriRingtone)
        val audioAttributes =
            AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
        ringtone.audioAttributes = audioAttributes
        ringtone.play()
    }

    private fun playVibrate(context: Context) {
        val vibrator =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // API Level 31에서 VibratorManager로 변경됨
                val vibratorManager =
                    context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                context.getSystemService(VIBRATOR_SERVICE) as Vibrator
            }
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}