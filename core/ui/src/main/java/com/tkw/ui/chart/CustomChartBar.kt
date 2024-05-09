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
import com.tkw.ui.util.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class CustomChartBar
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : BarChart(context, attrs, defStyle), BaseChart<BarEntry>{
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
            val xAxisRenderer = CustomXAxisRenderer(
                viewPortHandler, this, getTransformer(
                    YAxis.AxisDependency.LEFT
                )
            )
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

    fun getWeekDateList(date: String): Array<String> {
        val formatter = DateTimeFormatter.ofPattern("MM/dd")
        val localDate = DateTimeUtils.getDateFromFormat(date)
        val week = ArrayList<String>()
        for(i in 1..7) {
            week.add(localDate.with(DayOfWeek.of(i)).format(formatter))
        }
        return week.toArray(arrayOf())
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