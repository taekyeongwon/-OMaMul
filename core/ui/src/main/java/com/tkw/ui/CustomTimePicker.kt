package com.tkw.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.NumberPicker
import android.widget.TimePicker
import androidx.constraintlayout.widget.ConstraintLayout
import com.tkw.common.util.DateTimeUtils
import com.tkw.ui.databinding.CustomTimepickerBinding
import kotlin.math.min

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

        dataBinding.hour.setOnValueChangedListener { picker, oldVal, newVal ->
            if(newVal == 0) {
                dataBinding.minute.minValue = minMinuteBy0Hour
            } else {
                dataBinding.minute.minValue = minMinute
            }
        }
    }

    fun getValue(): Int {
        val hour = dataBinding.hour.value
        val minute = dataBinding.minute.value
        val time = DateTimeUtils.getTime(
            hour,
            minute,
            context.getString(R.string.hour),
            context.getString(R.string.minute)
        )
        val localTime = DateTimeUtils.getTimeFromLocalTime(
            time,
            context.getString(R.string.hour),
            context.getString(R.string.minute)
        )
        Log.d("test", time)
        Log.d("test", "${localTime.hour} : ${localTime.minute}")
        return 0
    }

//    private val onTimeChangedListener =
//        OnTimeChangedListener { view, hourOfDay, minute ->
//            if (hourOfDay < minHour || (hourOfDay == minHour && minute < minMinute)) {
//                view?.hour = minHour
//                view?.minute = minMinute
//            } else if (hourOfDay > maxHour || (hourOfDay == maxHour && minute > maxMinute)) {
//                view?.hour = maxHour
//                view?.minute = maxMinute
//            }
//        }

    init {
//        setIs24HourView(true)
//        setOnTimeChangedListener(onTimeChangedListener)
//        initMinMax()
    }

//    private fun initMinMax() {
//        try {
//            val classForId = Class.forName("com.android.internal.R\$id")
//            val fieldId = classForId.getField("hour").getInt(null)
//            this.findViewById<NumberPicker>(fieldId).apply {
//                minValue = this@CustomTimePicker.minHour
//                maxValue = this@CustomTimePicker.maxHour
//                displayedValues = getDisplayedValue()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    private fun getDisplayedValue(min: Int): Array<String> {
        val minuteArray = ArrayList<String>()
        for(i in min .. 59) {
            minuteArray.add(i.toString())
        }
        return minuteArray.toArray(arrayOf())
    }
}