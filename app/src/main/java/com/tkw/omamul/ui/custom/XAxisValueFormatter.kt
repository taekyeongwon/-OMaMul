package com.tkw.omamul.ui.custom

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class XAxisValueFormatter(private val unit: String): ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        val lastLabel = axis.mEntries[axis.mEntryCount - 1]
        return if(lastLabel == value)
            String.format("%.0f", value) + unit
        else String.format("%.0f", value)
    }
}