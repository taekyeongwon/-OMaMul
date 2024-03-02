package com.tkw.omamul.ui.water.main

import android.graphics.Color
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.FragmentLogDayBinding
import com.tkw.omamul.ui.base.BaseFragment
import com.tkw.omamul.ui.custom.CustomMarkerView
import com.tkw.omamul.ui.custom.CustomYAxisRenderer
import com.tkw.omamul.ui.custom.XAxisValueFormatter

class LogDayFragment: BaseFragment<FragmentLogDayBinding, WaterViewModel>(R.layout.fragment_log_day) {
    override val viewModel: WaterViewModel by viewModels { ViewModelFactory }

    override fun initView() {
        val list = ArrayList<BarEntry>()
        list.add(BarEntry(0f, 100f))
        list.add(BarEntry(2f, 200f))
        list.add(BarEntry(4f, 300f))

        val barDataSet = BarDataSet(list, "").apply {
            color = ColorTemplate.getHoloBlue()
            valueTextColor = Color.BLACK
            valueTextSize = 16f
            setDrawValues(false)
        }

        val barData = BarData(barDataSet)
        dataBinding.barChart.apply {
            data = barData
            setPinchZoom(false)
            setScaleEnabled(false)
            isDoubleTapToZoomEnabled = false
            legend.isEnabled = false
            description.isEnabled = false
            marker = CustomMarkerView(context, R.layout.custom_marker)
            axisRight.isEnabled = false
            axisLeft.apply {
                isEnabled = true
                labelCount = 4
                val yAxisRenderer = CustomYAxisRenderer(viewPortHandler, axisLeft, getTransformer(
                    YAxis.AxisDependency.LEFT))
                yAxisRenderer.setUnit(getString(R.string.unit_water))
                rendererLeftYAxis = yAxisRenderer
                setDrawAxisLine(false)
            }

            xAxis.apply {
                isEnabled = true
                position = XAxis.XAxisPosition.BOTTOM
                axisMaximum = 24f
                valueFormatter = XAxisValueFormatter()
                setDrawGridLines(false)
            }

            animateY(1000)
        }
    }

    override fun bindViewModel(binder: FragmentLogDayBinding) {

    }

    override fun initObserver() {

    }

    override fun initListener() {

    }
}