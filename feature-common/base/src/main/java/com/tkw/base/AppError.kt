package com.tkw.base

import android.content.Context

class AppError(private val code: Int, cause: Throwable? = null): Exception("", cause) {
    private val prefix = "app_error_"

    fun getMessage(context: Context) : String {
        return try {
            val ref = com.tkw.ui.R.string::class.java
            val field = ref.getField(prefix + code)
            val resId = field.getInt(null)
            context.getString(resId)
        } catch (e: Exception) {
            context.getString(com.tkw.ui.R.string.default_error)
        }
    }
}