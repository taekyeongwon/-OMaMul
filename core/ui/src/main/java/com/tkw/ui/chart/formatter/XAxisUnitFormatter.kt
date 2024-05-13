package com.tkw.ui.chart.formatter

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

//일, 월 차트 x라벨 포맷
class XAxisUnitFormatter(private val unit: String): ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        val lastLabel = axis.mEntries[axis.mEntries.size - 1]
        return if (lastLabel == value)
            String.format("%.0f", value) + unit
        else String.format("%.0f", value)
    }
}