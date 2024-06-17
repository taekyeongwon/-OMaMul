package com.tkw.common

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat


object NotificationManager {
    private const val CHANNEL_ID = "CHANNEL_ID"
    private const val NOTIFICATION_GROUP_NAME = "GROUP_NAME"

    fun createNotificationChannel(context: Context) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val name = "오마물"
        val description = "물 알람 채널입니다."
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            this.description = description
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun buildNotification(
        context: Context,
        drawable: Int,
        title: String,
        text: String
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setSmallIcon(drawable)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .setGroup(NOTIFICATION_GROUP_NAME)
            .setCategory(Notification.CATEGORY_ALARM)

        return builder
    }

    fun buildSummaryNotification(
        context: Context,
        drawable: Int
    ): NotificationCompat.Builder {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        builder.setSmallIcon(drawable)
            .setGroup(NOTIFICATION_GROUP_NAME)
            .setGroupSummary(true)

        return builder
    }
}