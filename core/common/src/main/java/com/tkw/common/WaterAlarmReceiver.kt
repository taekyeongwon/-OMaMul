package com.tkw.common

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class WaterAlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("alarm receiver", intent?.getStringExtra("test") ?: "null")
    }
}