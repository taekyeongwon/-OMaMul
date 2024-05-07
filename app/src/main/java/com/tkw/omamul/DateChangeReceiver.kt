package com.tkw.omamul

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * 날짜가 변경될 때 호출되는 리시버. 기기 설정에서 과거로 변경할 경우엔 호출되지 않는다.
 */
class DateChangeReceiver(private val listener: () -> Unit): BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == Intent.ACTION_DATE_CHANGED)
            listener()
    }
}