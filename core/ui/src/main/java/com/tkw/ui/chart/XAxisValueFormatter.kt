package com.tkw.ui.chart

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

//주 차트 x라벨 포맷
class XAxisValueFormatter(private val date: Array<String>): ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        return date[value.toInt() - 1]
    }
}