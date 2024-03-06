package com.tkw.omamul.util

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import androidx.fragment.app.DialogFragment

//https://yang-droid.tistory.com/31 참고
fun Context.dialogFragmentResize(
    dialogFragment: DialogFragment,
    width: Float
) {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val window = dialogFragment.dialog?.window
    val params = window?.attributes
    if (Build.VERSION.SDK_INT < 30) {
        val display = windowManager.defaultDisplay
        val size = Point()

        display.getSize(size)

        params?.width = (size.x * width).toInt()
    } else {
        val rect = windowManager.currentWindowMetrics.bounds

        params?.width = (rect.width() * width).toInt()
    }
    window?.attributes = params as LayoutParams
}