package com.tkw.util

import android.content.Context
import kotlin.math.round

object DimenUtils {
    fun dpToPx(context: Context, dp: Int): Float {
        val density = context.resources.displayMetrics.density
        return round(dp * density)
    }
}
