package com.tkw.common

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat


object NotificationManager {
    const val NOTI_CH = "NOTI_CH"
    const val MUTE_CH = "MUTE_CH"
    private const val NOTIFICATION_GROUP_NAME = "GROUP_NAME"
    private lateinit var homeIntent: PendingIntent
    const val TIMEOUT: Long = 1000 * 30

    fun setContentClickPendingIntent(intent: PendingIntent) {
        homeIntent = intent
    }

    fun createNotificationChannel(context: Context) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val name = context.getString(R.string.channel_name)
        val description = context.getString(R.string.channel_desc)
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
        val contentView = RemoteViews(context.packageName, R.layout.custom_notification)
        contentView.setTextViewText(R.id.tv_title, title)
        contentView.setTextViewText(R.id.tv_content, text)

        val builder = NotificationCompat.Builder(context, channelId)
        builder.setSmallIcon(drawable)
            .setContentTitle(title)
            .setContentText(text)
            .setCategory(Notification.CATEGORY_ALARM)
            .setContentIntent(homeIntent)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(contentView)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        return builder
    }

    fun NotificationCompat.Builder.fullScreenBuilder(
        context: Context,
        title: String,
        text: String
    ): NotificationCompat.Builder {
        val contentView = RemoteViews(context.packageName, R.layout.custom_notification)
        contentView.setTextViewText(R.id.tv_title, title)
        contentView.setTextViewText(R.id.tv_content, text)

        setCustomHeadsUpContentView(contentView)
        setFullScreenIntent(getFullScreenIntent(context), true)
        setTimeoutAfter(TIMEOUT)
        return this
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

    fun notify(context: Context) {
        val builder = buildNotification(
            context,
            R.drawable.noti_foreground,
            context.getString(R.string.notification_title),
            context.getString(R.string.notification_text),
            NOTI_CH //핸드폰 설정대로면 NOTI_CH, 그 외 MUTE_CH
        )
        //휴대폰 설정과 동일이라면 그대로 빌드.
        //알림 표시 안하는 경우 builder.setSilent(true) 적용 후
        //해당 링톤 모드에 맞게 아래 인스턴스 메서드 호출
//            builder.setSilent(true)
//            summaryBuilder.setSilent(true)
        if (canUseFullScreenIntent(context)) {
            builder.fullScreenBuilder(
                context,
                context.getString(R.string.notification_title),
                context.getString(R.string.notification_text)
            )
        }
        val notificationId = "${System.currentTimeMillis()}".hashCode()
        val notificationManager: NotificationManager =
            context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    fun canUseFullScreenIntent(context: Context): Boolean {
        return if(Build.VERSION.SDK_INT >= 34) {
            val notificationManager: NotificationManager =
                context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.canUseFullScreenIntent()
        } else {
            true
        }
    }

    fun isNotificationEnabled(context: Context): Boolean {
        return NotificationManagerCompat
            .from(context)
            .areNotificationsEnabled()
    }

    private fun getFullScreenIntent(context: Context): PendingIntent {
        val intent = Intent(context, AlarmActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return PendingIntent.getActivity(
            context,
            0x01,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}