package com.tkw.ui.chart

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.tkw.ui.chart.base.BaseChart
import com.tkw.ui.chart.base.MPLineChartBase
import com.tkw.ui.chart.formatter.XAxisUnitFormatter
import com.tkw.ui.chart.formatter.XAxisValueFormatter
import com.tkw.ui.chart.marker.MarkerType
import com.tkw.ui.chart.renderer.CustomXAxisRenderer
import com.tkw.ui.chart.renderer.CustomYAxisRenderer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

class CustomLineChart
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : MPLineChartBase(context, attrs, defStyle), BaseChart<Entry> {
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

    override fun parsingChartData(x: Float, y: Float): Entry {
        return Entry(x, y)
    }

    override fun setChartData(list: List<Entry>) {
        val lineDataSet = LineDataSet(list, "").apply {
            lineWidth = 4f
            circleRadius = 5f
            color = ColorTemplate.getHoloBlue()
            setCircleColors(ColorTemplate.getHoloBlue())
            valueTextColor = Color.BLACK
            valueTextSize = 16f
            setDrawValues(false)
        }
        data = LineData(lineDataSet)
        calculateYMaximum()
        animateY(1000)
    }

    override fun setXMinMax(min: Float, max: Float) {
        setChartXMinMax(min, max)
    }
}