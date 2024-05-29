package com.tkw.ui.alarm

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.tkw.ui.CustomCheckBox
import com.tkw.ui.databinding.CustomAlarmBinding

class CustomAlarmModeView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ConstraintLayout(context, attrs, defStyle) {
    private val dataBinding: CustomAlarmBinding

    init {
        dataBinding = CustomAlarmBinding.inflate(LayoutInflater.from(context), this, true)
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