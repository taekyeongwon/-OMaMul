package com.tkw.cup

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.tkw.ui.WaterAmountPicker

object CupBindingAdapter {
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

    @JvmStatic
    @BindingAdapter("unit")
    fun setUnit(view: TextView, value: Int) {
        view.text = "${value}ml"
    }
}