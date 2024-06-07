package com.tkw.record

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.tkw.common.util.DateTimeUtils

object LogBindingAdapter {
    @JvmStatic
    @BindingAdapter("weekDays")
    fun setWeekDays(view: TextView, value: String?) {
        if(value != null) {
            val week = DateTimeUtils.getWeekDates(value)
            view.text = "${week.first} - ${week.second}"
        }
    }

    @JvmStatic
    @BindingAdapter("monthDays")
    fun setMonthDays(view: TextView, value: String?) {
        if(value != null) {
            view.text = value.substring(0, 7)
        }
    }
}