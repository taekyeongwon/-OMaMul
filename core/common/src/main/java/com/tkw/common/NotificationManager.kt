package com.tkw.common

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.provider.Settings
import androidx.core.app.NotificationCompat


object NotificationManager {
    const val NOTI_CH = "NOTI_CH"
    const val MUTE_CH = "MUTE_CH"
    private const val NOTIFICATION_GROUP_NAME = "GROUP_NAME"

    fun createNotificationChannel(context: Context) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val name = "오마물"
        val description = "물 알람 채널입니다."
        val channel = NotificationChannel(NOTI_CH, name, importance).apply {
            this.description = description
        }
        val muteChannel = NotificationChannel(MUTE_CH, name, importance).apply {
            this.description = description
            setSound(null, null)
            enableVibration(false)
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        notificationManager.createNotificationChannel(muteChannel)
    }

    fun buildNotification(
        context: Context,
        drawable: Int,
        title: String,
        text: String,
        channelId: String
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, channelId)
        builder.setSmallIcon(drawable)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setGroup(NOTIFICATION_GROUP_NAME)
            .setCategory(Notification.CATEGORY_ALARM)
            .setOngoing(true)

        return builder
    }

    fun buildSummaryNotification(
        context: Context,
        drawable: Int,
        channelId: String
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, channelId)
        builder.setSmallIcon(drawable)
            .setGroup(NOTIFICATION_GROUP_NAME)
            .setGroupSummary(true)
        return builder
    }
}