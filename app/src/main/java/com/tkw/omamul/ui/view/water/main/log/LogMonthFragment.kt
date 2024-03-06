package com.tkw.omamul.ui.view.water.main.log

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.FragmentLogMonthBinding
import com.tkw.omamul.ui.custom.CustomMarkerView
import com.tkw.omamul.ui.custom.CustomYAxisRenderer
import com.tkw.omamul.ui.custom.XAxisValueFormatter
import com.tkw.omamul.ui.view.water.main.WaterViewModel
import com.tkw.omamul.common.autoCleared

class LogMonthFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentLogMonthBinding>()
    private val viewModel: WaterViewModel by activityViewModels { ViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentLogMonthBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        initView()
    }

    private fun initBinding() {
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@LogMonthFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initView() {
        val list = ArrayList<BarEntry>()
        list.add(BarEntry(0f, 100f))
        list.add(BarEntry(2f, 200f))
        list.add(BarEntry(4f, 300f))
        list.add(BarEntry(6f, 400f))
        list.add(BarEntry(10f, 430f))

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
}