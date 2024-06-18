package com.tkw.common

import android.annotation.SuppressLint
import android.app.Application
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
import androidx.core.app.NotificationManagerCompat

class WaterAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //todo extra로 받아서 buildNotification에 ringtone mode 넘겨줌
        if (context != null) {
            val builder = NotificationManager.buildNotification(
                context,
                R.drawable.noti_foreground,
                "오마물",
                "물 마실 시간 입니다.",
                NotificationManager.NOTI_CH //핸드폰 설정대로면 NOTI_CH, 그 외 MUTE_CH
            )

            //휴대폰 설정과 동일이라면 그대로 빌드.
            //알림 표시 안하는 경우 builder.setSilent(true) 적용 후
            //해당 링톤 모드에 맞게 아래 인스턴스 메서드 호출
//            builder.setSilent(true)
//            summaryBuilder.setSilent(true)
            val notificationManager: android.app.NotificationManager =
                context.getSystemService(Application.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.notify(0, builder.build())
//            if(Build.VERSION.SDK_INT >= 34)
//                Log.d("alarm receiver", notificationManager.canUseFullScreenIntent().toString())
        }

        Log.d("alarm receiver", "received")

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