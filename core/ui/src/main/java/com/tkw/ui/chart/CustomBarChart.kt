package com.tkw.ui.chart

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.tkw.ui.chart.base.BaseChart
import com.tkw.ui.chart.base.MPBarChartBase
import com.tkw.ui.chart.marker.MarkerType

class CustomBarChart
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : MPBarChartBase(context, attrs, defStyle), BaseChart<BarEntry> {

    override fun setLimit(limit: Float) {
        setChartLimit(limit)
    }

    override fun setUnit(xUnit: String, yUnit: String) {
        setChartUnit(xUnit, yUnit)
    }

    override fun setYUnit(yUnit: String) {
        setChartYUnit(yUnit)
    }

    override fun setXValueFormat(values: Array<String>) {
        setChartXValueFormat(values)
    }

    override fun setMarker(markerType: MarkerType) {
        setChartMarker(markerType)
    }

    override fun parsingChartData(x: Float, y: Float): BarEntry {
        return BarEntry(x, y)
    }

    override fun setChartData(list: List<BarEntry>) {
        val sortedList = list.sortedBy { it.x }
        val barDataSet = BarDataSet(sortedList, "").apply {
            color = ColorTemplate.getHoloBlue()
            valueTextColor = Color.BLACK
            valueTextSize = 16f
            setDrawValues(false)
        }
        data = BarData(barDataSet)
        data.barWidth = 0.5f
        calculateYMaximum()
        animateY(1000)
    }

    override fun setXMinMax(min: Float, max: Float) {
        setChartXMinMax(min, max)
    }
}