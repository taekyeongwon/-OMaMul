package com.tkw.omamul

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Process
import android.util.Log
import androidx.navigation.NavDeepLinkBuilder
import com.tkw.common.NotificationManager
import dagger.hilt.android.HiltAndroidApp
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess

@HiltAndroidApp
class MainApplication: Application() {
    companion object {
        var sharedPref: SharedPreferences? = null
    }

    override fun onCreate() {
        super.onCreate()
        sharedPref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        setUncaughtExceptionHandler()
        initNotification()
    }

    private fun setUncaughtExceptionHandler() {
        val defaultHandler: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.d(thread.name, getStackTrace(throwable))

            defaultHandler?.uncaughtException(thread, throwable)
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }
    }

    private fun initNotification() {
        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(com.tkw.home.R.navigation.home_nav_graph)
            .setDestination(com.tkw.home.R.id.waterFragment)
            .createPendingIntent()
        NotificationManager.createNotificationChannel(this)
        NotificationManager.setContentClickPendingIntent(pendingIntent)
    }

    private fun getStackTrace(e: Throwable?): String {
        val result = StringWriter()
        val printWriter = PrintWriter(result)

        var th: Throwable? = e
        while(th != null) {
            th.printStackTrace(printWriter)
            th = th.cause
        }

        val stackTraceAsString = result.toString()
        printWriter.close()

        return stackTraceAsString
    }
}