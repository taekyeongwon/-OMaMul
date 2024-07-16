package com.tkw.alarm.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.tkw.alarm.R
import com.tkw.alarm.databinding.CustomAlarmModeBinding
import com.tkw.ui.CustomCheckBox

class AlarmModeView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    ConstraintLayout(context, attrs, defStyle) {
    private val dataBinding: CustomAlarmModeBinding
    private val weekList: ArrayList<CustomCheckBox> = arrayListOf()

    init {
        dataBinding = CustomAlarmModeBinding.inflate(LayoutInflater.from(context), this, true)
        initWeekList()
    }

    private fun initWeekList() {
        for(i in 0 until dataBinding.root.childCount) {
            val view = dataBinding.root.getChildAt(i)
            if(view is CustomCheckBox) {
                weekList.add(view)
            }
        }
    }

    fun setChecked(list: List<Int>) {
        list.forEach {
            weekList[it].setChecked(true)
        }
    }

    fun setCheckListListener(block: (ArrayList<Int>) -> Unit) {
        weekList.forEach {
            it.setCheckBoxClickListener { block(getCheckedList()) }
        }
    }

    fun getCheckedList(): ArrayList<Int> {
        val result = ArrayList<Int>()
        for(i in 0 until weekList.size) {
            if(weekList[i].getChecked()) {
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

    fun setPeriodTime(time: String) {
        dataBinding.tvIntervalSet.text = time
    }

    fun getPeriodTime(): String = dataBinding.tvIntervalSet.text.toString()

    fun setPeriodClickListener(block: () -> Unit) {
        dataBinding.clPeriod.setOnClickListener { block() }
    }
}