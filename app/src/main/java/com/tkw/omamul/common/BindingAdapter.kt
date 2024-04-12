package com.tkw.omamul.common

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.tkw.omamul.common.util.DateTimeUtils
import com.tkw.omamul.ui.custom.WaterAmountPicker

object BindingAdapter {
    /**
     * 물의 양 number picker 양방향 바인딩
     */
    @JvmStatic
    @BindingAdapter("value")
    fun setValue(view: WaterAmountPicker, value: Int) {
        val old = view.value
        if(old != value) {
            view.value = value
        }
    }

    @JvmStatic
    @BindingAdapter("valueAttrChanged")
    fun setValueChanged(view: WaterAmountPicker, listener: InverseBindingListener) {
        view.setOnValueChangedListener { _, _, _ ->
            listener.onChange()
        }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "value", event = "valueAttrChanged")
    fun getValue(view: WaterAmountPicker): Int {
        return view.getCurrentValue()
    }
    //end

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