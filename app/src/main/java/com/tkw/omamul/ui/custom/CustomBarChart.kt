package com.tkw.omamul.ui.custom

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.tkw.omamul.R

class CustomBarChart
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : BarChart(context, attrs, defStyle) {
    private var limit: Float = 0f

    init {
        initDefault()
        initXAxis()
        initYAxis()
        setLimit(2000f)
        animateY(1000)
    }

    private fun initDefault() {
        setPinchZoom(false)
        setScaleEnabled(false)
        setExtraOffsets(10f, 100f, 20f, 10f)
        marker = CustomMarkerView(context, R.layout.custom_marker)
        isDoubleTapToZoomEnabled = false
        legend.isEnabled = false
        description.isEnabled = false
        axisRight.isEnabled = false
    }

    private fun initXAxis() {
        xAxis.apply {
            isEnabled = true
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = XAxisValueFormatter()
            setDrawGridLines(false)
            axisMinimum = 0f
            axisMaximum = 24f
        }
    }

    private fun initYAxis() {
        axisLeft.apply {
            isEnabled = true
            setLabelCount(5, true)
            val yAxisRenderer = CustomYAxisRenderer(viewPortHandler, axisLeft, getTransformer(
                YAxis.AxisDependency.LEFT))
            yAxisRenderer.setUnit(context.getString(R.string.unit_water))
            rendererLeftYAxis = yAxisRenderer
            setDrawAxisLine(false)
            axisMinimum = 0f
        }
    }

    fun setLimit(limit: Float) {
        this.limit = limit
        axisLeft.addLimitLine(LimitLine(limit).apply {
            lineWidth = 1f
            enableDashedLine(10f, 10f, 0f)
            lineColor = Color.DKGRAY
        })
    }

    fun parsingChartData(x: Float, y: Float): BarEntry {
        return BarEntry(x, y)
    }

    fun setChartData(list: List<BarEntry>) {
        val barDataSet = BarDataSet(list, "").apply {
            color = ColorTemplate.getHoloBlue()
            valueTextColor = Color.BLACK
            valueTextSize = 16f
            setDrawValues(false)
        }
        data = BarData(barDataSet)
        calculateYMaximum()
    }

    /**
     * 일 차트인 경우 0 ~ 24
     * 주 차트인 경우 선택한 주의 날짜 ex) 1 ~ 7
     * 월 차트인 경우 1 ~ 30(31) 달에 맞춰서 설정
     */
    fun setXMinMax(min: Float, max: Float) {
        xAxis.apply {
            axisMinimum = min
            axisMaximum = max
        }
    }

    private fun calculateYMaximum() {
        val maxAmount = data.yMax
        val newLimit = if(maxAmount < limit) limit else maxAmount
        axisLeft.axisMaximum = (newLimit / 4) * 5
    }
}