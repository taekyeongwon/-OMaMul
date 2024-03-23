package com.tkw.omamul.common

import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.tkw.omamul.ui.custom.WaterAmountPicker

object BindingAdapter {
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
}