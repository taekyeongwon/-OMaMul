package com.tkw.ui.chart

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.tkw.ui.R

class CustomBarChart
    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : BarChart(context, attrs, defStyle) {
    private var limit: Float = 0f
    private lateinit var yAxisRenderer: CustomYAxisRenderer

    init {
        initDefault()
        initXAxis()
        initYAxis()
    }

    private fun initDefault() {
        setPinchZoom(false)
        setScaleEnabled(false)
        setExtraOffsets(10f, 100f, 20f, 10f)
        isDoubleTapToZoomEnabled = false
        legend.isEnabled = false
        description.isEnabled = false
        axisRight.isEnabled = false
        yAxisRenderer = CustomYAxisRenderer(
            viewPortHandler,
            axisLeft,
            getTransformer(YAxis.AxisDependency.LEFT)
        )
        setLimit(2000f)
    }

    private fun initXAxis() {
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawAxisLine(false)
            val xAxisRenderer = CustomXAxisRenderer(viewPortHandler, this, getTransformer(
                YAxis.AxisDependency.LEFT))
            setXAxisRenderer(xAxisRenderer)
            labelCount = 7
        }
    }

    private fun initYAxis() {
        axisLeft.apply {
            setLabelCount(5, true)
            rendererLeftYAxis = yAxisRenderer
            setDrawAxisLine(false)
            axisMinimum = 0f
        }
    }

    fun setLimit(limit: Float) {
        this.limit = limit
        axisLeft.removeAllLimitLines()
        axisLeft.addLimitLine(LimitLine(limit).apply {
            lineWidth = 1f
            enableDashedLine(10f, 10f, 0f)
            lineColor = Color.DKGRAY
        })
    }

    fun setUnit(xUnit: String, yUnit: String) {
        xAxis.valueFormatter = XAxisValueFormatter(xUnit)
        yAxisRenderer.setUnit(yUnit)
    }

    fun setMarker(markerView: MarkerView) {
        markerView.chartView = this
        this.marker = markerView
    }

    fun parsingChartData(x: Float, y: Float): BarEntry {
        return BarEntry(x, y)
    }

    fun setChartData(list: List<BarEntry>) {
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

    /**
     * 일 차트인 경우 0 ~ 24
     * 주 차트인 경우 선택한 주의 날짜 ex) 1 ~ 7
     * 월 차트인 경우 1 ~ 31
     *
     * 양쪽 끝이 바 가운데 위치하도록 0.5씩 증감
     */
    fun setXMinMax(min: Float, max: Float) {
        xAxis.apply {
            axisMinimum = min - 0.5f
            axisMaximum = max + 0.5f
        }
    }

    private fun calculateYMaximum() {
        val maxAmount = data.yMax
        val newLimit = if(maxAmount < limit) limit else maxAmount
        axisLeft.axisMaximum = (newLimit / 4) * 5
    }
}