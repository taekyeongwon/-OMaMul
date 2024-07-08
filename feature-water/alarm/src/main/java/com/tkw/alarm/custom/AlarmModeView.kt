package com.tkw.alarm.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
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

    fun setCheckListListener(block: (ArrayList<Int>) -> Unit) {
        for(i in 0 until childCount) {
            val view = getChildAt(i)
            if(view is CustomCheckBox) {
                view.setOnClickListener { block(getCheckedList()) }
            }
        }
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

    fun setPeriodLayoutVisibility(flag: Boolean) {
        dataBinding.clPeriod.visibility =
            if(flag) View.VISIBLE
            else View.GONE
    }

    fun setPeriodClickListener(block: () -> Unit) {
        dataBinding.clPeriod.setOnClickListener { block() }
    }
}