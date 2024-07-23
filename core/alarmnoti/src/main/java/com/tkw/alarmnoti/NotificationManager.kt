package com.tkw.alarmnoti

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.tkw.domain.model.RingTone
import com.tkw.domain.model.RingToneMode

object NotificationManager {
    private const val NOTI_CH = "NOTI_CH"
    private const val MUTE_CH = "MUTE_CH"
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

    private fun buildNotification(
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

    private fun NotificationCompat.Builder.fullScreenBuilder(
        context: Context,
        title: String,
        text: String
    ): NotificationCompat.Builder {
        val contentView = RemoteViews(context.packageName, R.layout.custom_notification)
        contentView.setTextViewText(R.id.tv_title, title)
        contentView.setTextViewText(R.id.tv_content, text)

//        setCustomHeadsUpContentView(contentView)
        setFullScreenIntent(getFullScreenIntent(context), true)
//        setTimeoutAfter(TIMEOUT)
        return this
    }

    /**
     * 그룹으로 묶어서 알람 처리하는 경우 호출
     */
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

    fun notify(context: Context, ringtoneMode: RingToneMode) {
        val builder = buildNotification(
            context,
            R.drawable.noti_foreground,
            context.getString(R.string.notification_title),
            context.getString(R.string.notification_text),
            getChannel(ringtoneMode.getCurrentMode().name) //핸드폰 설정대로면 NOTI_CH, 그 외 MUTE_CH
        )
        //휴대폰 설정과 동일이라면 그대로 빌드.
        //알림 표시 안하는 경우 builder.setSilent(true) 적용
        if(ringtoneMode.isSilence) {
            builder.setSilent(true)
        }
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
        play(ringtoneMode.getCurrentMode().name, context)
        Handler(Looper.getMainLooper()).postDelayed({
            notifyIfFullScreen(context, notificationId)
        }, 5000)
    }

    private fun notifyIfFullScreen(context: Context, notificationId: Int) {
        val builder = buildNotification(
            context,
            R.drawable.noti_foreground,
            context.getString(R.string.notification_title),
            context.getString(R.string.notification_text),
            MUTE_CH
        )
        val notificationManager: NotificationManager =
            context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, builder.build())
    }

    private fun canUseFullScreenIntent(context: Context): Boolean {
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

    /**
     * ringtone mode
     * 핸드폰 설정과 동일한 경우 NOTI_CH 리턴
     * 이외 MUTE_CH 리턴
     */
    private fun getChannel(ringtoneMode: String): String {
        return when(ringtoneMode) {
            RingTone.DEVICE.name -> NOTI_CH
            else -> MUTE_CH
        }
    }

    private fun play(ringtoneMode: String, context: Context) {
        when(ringtoneMode) {
            RingTone.BELL.name -> {
                playRingtone(context)
            }
            RingTone.VIBE.name -> {
                playVibrate(context)
            }
            RingTone.ALL.name -> {
                playRingtone(context)
                playVibrate(context)
            }
        }
    }

    /**
     * 아래 두 메서드는 notify 시 호출
     */
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
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
        vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}