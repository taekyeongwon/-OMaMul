package com.tkw.alarmnoti

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationDismissedReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(context != null && intent != null) {
            val notificationId = intent.getIntExtra("notification_id", -1)
            NotificationManager.notifyIfFullScreen(context, notificationId)
        }
    }
}