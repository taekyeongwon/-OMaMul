package com.tkw.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.tkw.common.util.DateTimeUtils
import com.tkw.ui.R
import com.tkw.ui.databinding.CustomTimepickerBinding

class CustomTimePicker
@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attrs, defStyle) {

    private val minHour = 0
    private val minMinute = 0
    private val minMinuteBy0Hour = 10
    private val maxHour = 4
    private val maxMinute = 59
    private val initHour = 0
    private val initMinute = 10

    private val dataBinding: CustomTimepickerBinding

    init {
        dataBinding = CustomTimepickerBinding.inflate(LayoutInflater.from(context), this, true)
        initView()
    }

    private fun initView() {
        dataBinding.hour.minValue = minHour
        dataBinding.hour.maxValue = maxHour
        dataBinding.minute.minValue = minMinuteBy0Hour
        dataBinding.minute.maxValue = maxMinute
        dataBinding.hour.value = initHour
        dataBinding.minute.value = initMinute

        dataBinding.hour.setOnValueChangedListener { _, _, newVal ->
            if(newVal == 0) {
                dataBinding.minute.minValue = minMinuteBy0Hour
            } else {
                dataBinding.minute.minValue = minMinute
            }
        }
    }

    fun setValue(hour: Int, min: Int) {
        if(hour == 0) {
            dataBinding.minute.minValue = minMinuteBy0Hour
        } else {
            dataBinding.minute.minValue = minMinute
        }
        dataBinding.hour.value = hour
        dataBinding.minute.value = min
    }

    fun getValue(): String {
        val hour = dataBinding.hour.value
        val minute = dataBinding.minute.value
        return DateTimeUtils.Time.getFormat(
            hour,
            minute,
            context.getString(R.string.hour),
            context.getString(R.string.minute)
        )
    }
}