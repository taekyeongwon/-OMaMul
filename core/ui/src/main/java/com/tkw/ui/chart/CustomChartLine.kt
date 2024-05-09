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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CustomChartLine
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : LineChart(context, attrs, defStyle), BaseChart<Entry>{
    private var limit: Float = 0f
    private lateinit var yAxisRenderer: CustomYAxisRenderer

    @Inject
    @DayMarker
    lateinit var dayMarker: MarkerView

    @Inject
    @WeekMarker
    lateinit var weekMarker: MarkerView

    @Inject
    @MonthMarker
    lateinit var monthMarker: MarkerView

    init {
        initDefault()
        initXAxis()
        initYAxis()
    }

    override fun initDefault() {
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

    override fun initXAxis() {
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

    override fun initYAxis() {
        axisLeft.apply {
            setLabelCount(5, true)
            rendererLeftYAxis = yAxisRenderer
            setDrawAxisLine(false)
            axisMinimum = 0f
        }
    }

    override fun setLimit(limit: Float) {
        this.limit = limit
        axisLeft.removeAllLimitLines()
        axisLeft.addLimitLine(LimitLine(limit).apply {
            lineWidth = 1f
            enableDashedLine(10f, 10f, 0f)
            lineColor = Color.DKGRAY
        })
    }

    override fun setUnit(xUnit: String, yUnit: String) {
        xAxis.valueFormatter = XAxisUnitFormatter(xUnit)
        yAxisRenderer.setUnit(yUnit)
    }

    override fun setYUnit(yUnit: String) {
        yAxisRenderer.setUnit(yUnit)
    }

    override fun setXValueFormat(values: Array<String>) {
        xAxis.valueFormatter = XAxisValueFormatter(values)
    }

    override fun setMarker(markerType: MarkerType) {
        val marker = when(markerType) {
            MarkerType.DAY -> dayMarker
            MarkerType.WEEK -> weekMarker
            MarkerType.MONTH -> monthMarker
        }
        marker.chartView = this
        this.marker = marker
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
        xAxis.apply {
            axisMinimum = min - 0.5f
            axisMaximum = max + 0.5f
        }
    }

    override fun calculateYMaximum() {
        val maxAmount = data.yMax
        val newLimit = if(maxAmount < limit) limit else maxAmount
        axisLeft.axisMaximum = (newLimit / 4) * 5
    }
}