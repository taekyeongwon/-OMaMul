package com.tkw.common

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import androidx.fragment.app.DialogFragment

interface DialogResize {
    fun onResize(
        dialogFragment: DialogFragment,
        width: Float)
}

//https://yang-droid.tistory.com/31 참고
class DialogResizeImpl: DialogResize {
    override fun onResize(
        dialogFragment: DialogFragment,
        width: Float
    ) {
        val window = dialogFragment.dialog?.window
        val windowManager = dialogFragment.requireContext()
            .getSystemService(Context.WINDOW_SERVICE) as WindowManager
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
        window?.attributes = params as WindowManager.LayoutParams
    }
}