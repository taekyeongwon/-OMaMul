package com.tkw.ui.chart.base

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.tkw.ui.chart.formatter.XAxisUnitFormatter
import com.tkw.ui.chart.formatter.XAxisValueFormatter
import com.tkw.ui.chart.marker.DayMarkerView
import com.tkw.ui.chart.marker.MarkerType
import com.tkw.ui.chart.marker.MonthMarkerView
import com.tkw.ui.chart.marker.WeekMarkerView
import com.tkw.ui.chart.renderer.CustomXAxisRenderer
import com.tkw.ui.chart.renderer.CustomYAxisRenderer

/**
 * MPAndroidChart 라이브러리 공통 세팅용 클래스. BarChart 설정 관련.
 */
open class MPBarChartBase
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : BarChart(context, attrs, defStyle) {
    private var limit: Float = 0f
    private lateinit var yAxisRenderer: CustomYAxisRenderer

    private val dayMarker: MarkerView by lazy { DayMarkerView(context) }
    private val weekMarker: MarkerView by lazy { WeekMarkerView(context) }
    private val monthMarker: MarkerView by lazy { MonthMarkerView(context) }

    init {
        initDefault()
        initXAxis()
        initYAxis()
    }

    /**
     * zoom, offset 등 기본 세팅.
     * label count는 x축 7, y축 5 고정
     */
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
        setChartLimit(2000f)
    }

    /**
     * x축 세팅
     */
    private fun initXAxis() {
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawAxisLine(false)
            val xAxisRenderer = CustomXAxisRenderer(
                viewPortHandler, this, getTransformer(
                    YAxis.AxisDependency.LEFT
                )
            )
            setXAxisRenderer(xAxisRenderer)
            labelCount = 7
        }
    }

    /**
     * y축 세팅
     */
    private fun initYAxis() {
        axisLeft.apply {
            setLabelCount(5, true)
            rendererLeftYAxis = yAxisRenderer
            setDrawAxisLine(false)
            axisMinimum = 0f
        }
    }

    fun setChartLimit(limit: Float) {
        this.limit = limit
        axisLeft.removeAllLimitLines()
        axisLeft.addLimitLine(LimitLine(limit).apply {
            lineWidth = 1f
            enableDashedLine(10f, 10f, 0f)
            lineColor = Color.DKGRAY
        })
    }

    protected fun setChartUnit(xUnit: String, yUnit: String) {
        xAxis.valueFormatter = XAxisUnitFormatter(xUnit)
        yAxisRenderer.setUnit(yUnit)
    }

    protected fun setChartYUnit(yUnit: String) {
        yAxisRenderer.setUnit(yUnit)
    }

    protected fun setChartXValueFormat(values: Array<String>) {
        xAxis.valueFormatter = XAxisValueFormatter(values)
    }

    protected fun setChartMarker(markerType: MarkerType) {
        val marker = when(markerType) {
            MarkerType.DAY -> dayMarker
            MarkerType.WEEK -> weekMarker
            MarkerType.MONTH -> monthMarker
        }
        marker.chartView = this
        this.marker = marker
    }

    protected fun setChartXMinMax(min: Float, max: Float) {
        xAxis.apply {
            axisMinimum = min - 0.5f
            axisMaximum = max + 0.5f
        }
    }

    /**
     * limit보다 값이 커지는 경우 y축 5번째 값 계산 하기 위한 함수
     */
    protected fun calculateYMaximum() {
        val maxAmount = data.yMax
        val newLimit = if(maxAmount < limit) limit else maxAmount
        axisLeft.axisMaximum = (newLimit / 4) * 5
    }
}