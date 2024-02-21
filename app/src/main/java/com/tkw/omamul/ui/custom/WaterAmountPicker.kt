package com.tkw.omamul.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.widget.NumberPicker
import com.tkw.omamul.R

class WaterAmountPicker
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = android.R.attr.numberPickerStyle)
    : NumberPicker(context, attrs, defStyle) {  //최소, 최대 및 단위, 값 간격 변경 가능하도록, 현재값 가져오기

    private var minValue = 0
    private var maxValue = 0
    private var interval = 0

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.WaterAmountPicker, 0, 0)
            .apply {
                try {
                    minValue = getInt(R.styleable.WaterAmountPicker_minValue, 100)
                    maxValue = getInt(R.styleable.WaterAmountPicker_maxValue, 3000)
                    interval = getInt(R.styleable.WaterAmountPicker_interval, 5)

                    initNumberPicker()
                } finally {
                    recycle()
                }
            }
    }

    private fun initNumberPicker() {
        val values = getIntervalDisplayedValues(interval)
        setMinValue(0)
        setMaxValue(values.size - 1)
        value = values.size / 2
        displayedValues = values.toTypedArray()
        wrapSelectorWheel = false
    }

    fun getIntervalDisplayedValues(interval: Int): ArrayList<String> {
        val displayedArray = arrayListOf(minValue.toString())
        val index = (maxValue - minValue) / interval
        for(i in 1 .. index) {
            val value = minValue + (i * interval)
            displayedArray.add(value.toString())
        }
        return displayedArray
    }

    fun getCurrentValue(): Int {
        return displayedValues[value].toInt()
    }
}