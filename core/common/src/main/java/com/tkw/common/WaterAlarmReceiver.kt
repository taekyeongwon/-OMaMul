package com.tkw.common

import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat

class WaterAlarmReceiver: BroadcastReceiver() {
//    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context != null) {
            val builder = NotificationManager.buildNotification(
                context,
                R.drawable.noti_foreground,
                "오마물",
                "물 마실 시간 입니다."
            )
            val summaryBuilder = NotificationManager.buildSummaryNotification(
                context,
                R.drawable.noti_foreground
            )

            val notificationId = "${System.currentTimeMillis()}".hashCode()
//            with(NotificationManagerCompat.from(context)) {
//                notify(notificationId, builder.build())
//                notify(0, summaryBuilder.build())
//            }
            val notificationManager: android.app.NotificationManager =
                context.getSystemService(Application.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.notify(notificationId, builder.build())
            notificationManager.notify(0, summaryBuilder.build())
        }

        Log.d("alarm receiver", "received")
    }
}