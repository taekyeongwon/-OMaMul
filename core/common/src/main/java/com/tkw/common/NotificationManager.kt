package com.tkw.common

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat


object NotificationManager {
    const val NOTI_CH = "NOTI_CH"
    const val MUTE_CH = "MUTE_CH"
    private const val NOTIFICATION_GROUP_NAME = "GROUP_NAME"
    private lateinit var homeIntent: PendingIntent

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

    fun setPendingIntent(intent: PendingIntent) {
        homeIntent = intent
    }

    fun buildNotification(
        context: Context,
        drawable: Int,
        title: String,
        text: String,
        channelId: String
    ): NotificationCompat.Builder {
        val contentView = RemoteViews(context.packageName, R.layout.custom_notification)
        contentView.setTextViewText(R.id.tv_title, title)
        contentView.setTextViewText(R.id.tv_content, text)

        val builder = NotificationCompat.Builder(context, channelId)
        builder.setSmallIcon(drawable)
            .setCategory(Notification.CATEGORY_ALARM)
            .setContentIntent(homeIntent)
            .setFullScreenIntent(homeIntent, true)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomHeadsUpContentView(contentView)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)    //테스트 필요

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