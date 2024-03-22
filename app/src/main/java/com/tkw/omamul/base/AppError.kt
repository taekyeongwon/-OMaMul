package com.tkw.omamul.base

import android.content.Context
import com.tkw.omamul.R

class AppError(private val code: Int, cause: Throwable? = null): Exception("", cause) {
    private val prefix = "app_error_"

    fun getMessage(context: Context) : String {
        return try {
            val ref = R.string::class.java
            val field = ref.getField(prefix + code)
            val resId = field.getInt(null)
            context.getString(resId)
        } catch (e: Exception) {
            context.getString(R.string.default_error)
        }
    }
}