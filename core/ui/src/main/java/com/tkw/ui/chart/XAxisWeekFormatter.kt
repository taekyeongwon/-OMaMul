package com.tkw.ui.chart

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter
import com.tkw.util.DateTimeUtils
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

//주 차트 x라벨 포맷
class XAxisWeekFormatter(private val date: String): ValueFormatter() {
    override fun getAxisLabel(value: Float, axis: AxisBase): String {
        val weekList = getWeekDateList()
        return weekList[value.toInt() - 1]
    }

    //주 단위인 경우 MM/dd형태로 표시
    private fun getWeekDateList(): Array<String> {
        val formatter = DateTimeFormatter.ofPattern("MM/dd")
        val localDate = DateTimeUtils.getDateFromFormat(date)
        val week = ArrayList<String>()
        for(i in 1..7) {
            week.add(localDate.with(DayOfWeek.of(i)).format(formatter))
        }
        return week.toArray(arrayOf())
    }
}