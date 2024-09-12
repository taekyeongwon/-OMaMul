package com.tkw.ui.custom

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import com.tkw.ui.R

class WaterAmountPicker
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = android.R.attr.numberPickerStyle)
    : NumberPicker(context, attrs, defStyle) {  //최소, 최대 및 단위, 값 간격 변경 가능하도록, 현재값 가져오기

    private var minValue = 0
    private var maxValue = 0
    private var interval = 0

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.WaterAmountPicker,
            0,
            0
        )
        with(typedArray) {
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
        displayedValues = values.toTypedArray()
        setMinValue(0)
        setMaxValue(values.size - 1)
        value = (maxValue - minValue) / 2
        wrapSelectorWheel = false
        setInputTypeNumber(this)
    }

    /**
     * xml에서 value값 안 줬다면 최대~최소값의 평균 값으로 initNumberPicker에서 기본값 설정.
     * xml에서 value값 줬다면 해당 값으로 설정
     *
     * displayedValues를 설정함으로써 value에 값을 set하면 해당 값이 배열의 인덱스로 들어가게 됨.
     * 직접 값을 세팅 해주기 위해 재정의.
     */
    override fun setValue(value: Int) {
        val newValue = (value - minValue) / interval
        super.setValue(newValue)
    }

    //인덱스 값이 아닌 displayedValue 값 리턴
    fun getCurrentValue(): Int {
        return displayedValues[super.getValue()].toInt()
    }

    fun getIntervalDisplayedValues(interval: Int): ArrayList<String> {
        val displayedArray = arrayListOf<String>()
        val index = (maxValue - minValue) / interval
        for(i in 0 .. index) {
            val value = minValue + (i * interval)
            displayedArray.add(value.toString())
        }
        return displayedArray
    }

    /**
     * number picker 선택 시 키보드 inputType 설정
     * https://stackoverflow.com/questions/16793414/android-number-picker-keyboard-type
     */
    private fun setInputTypeNumber(vg: ViewGroup) {
        (0..vg.childCount).map { vg.getChildAt(it) }.forEach {
            when (it) {
                is ViewGroup -> setInputTypeNumber(it) // recurse
                is EditText -> it.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }
    }
}