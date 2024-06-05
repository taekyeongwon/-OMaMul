package com.tkw.alarm.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.tkw.alarm.databinding.CustomAlarmModeBinding
import com.tkw.ui.CustomCheckBox

class AlarmModeView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ConstraintLayout(context, attrs, defStyle) {
    private val dataBinding: CustomAlarmModeBinding

    init {
        dataBinding = CustomAlarmModeBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun getCheckedList(): ArrayList<Int> {
        val result = ArrayList<Int>()
        val checkBoxArray = ArrayList<CustomCheckBox>()
        for(i in 0 until childCount) {
            val view = getChildAt(i)
            if(view is CustomCheckBox) {
                checkBoxArray.add(view)
            }
        }
        for(i in 0 until checkBoxArray.size) {
            if(checkBoxArray[i].getChecked()) {
                result.add(i)
            }
        }
        return result
    }
}