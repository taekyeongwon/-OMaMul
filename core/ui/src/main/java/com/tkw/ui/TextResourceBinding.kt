package com.tkw.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter

object TextResourceBinding {

    @JvmStatic
    @BindingAdapter(value = ["prefix", "textRes"], requireAll = false)
    fun TextView.setTextResource(prefix: String?, text: String) {
        this.text = String.format("%s%s", prefix ?: "", text)
    }

    @JvmStatic
    @BindingAdapter("resId")
    fun TextView.setTextResource(resId: Int?) {
        if(resId != null) {
            val text = context.getString(resId)
            this.text = text
        }
    }
}