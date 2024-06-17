package com.tkw.common

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class WaterAlarmReceiver: BroadcastReceiver() {
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
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                checkPermission(context, notificationId, builder, summaryBuilder)
            } else {
                with(NotificationManagerCompat.from(context)) {
                    notify(notificationId, builder.build())
                    notify(0, summaryBuilder.build())
                }
            }
//            val notificationManager: android.app.NotificationManager =
//                context.getSystemService(Application.NOTIFICATION_SERVICE) as android.app.NotificationManager
//            notificationManager.notify(notificationId, builder.build()) 이걸로 했을 때 권한 해제하면?
        }

        Log.d("alarm receiver", "received")
    }

    private fun checkPermission(
        context: Context,
        notificationId: Int,
        builder: NotificationCompat.Builder,
        summaryBuilder: NotificationCompat.Builder
    ) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(context)) {
                notify(notificationId, builder.build())
                notify(0, summaryBuilder.build())
            }
        }
    }
}