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
import com.tkw.omamul.util.setValueAnimator
import com.tkw.omamul.data.model.WaterEntity
import com.tkw.omamul.databinding.FragmentLogDayBinding
import com.tkw.omamul.ui.view.water.main.log.adapter.DayListAdapter
import com.tkw.omamul.ui.custom.CustomMarkerView
import com.tkw.omamul.ui.custom.CustomYAxisRenderer
import com.tkw.omamul.ui.custom.DividerDecoration
import com.tkw.omamul.ui.custom.XAxisValueFormatter
import com.tkw.omamul.ui.dialog.LogEditBottomDialog
import com.tkw.omamul.ui.view.water.main.WaterViewModel
import com.tkw.omamul.util.autoCleared

class LogDayFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentLogDayBinding>()
    private val viewModel: WaterViewModel by activityViewModels { ViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentLogDayBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        initView()
        initListener()
    }

    private fun initBinding() {
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@LogDayFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initView() {
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
            setExtraOffsets(10f, 100f, 20f, 10f)
            axisLeft.apply {
                isEnabled = true
                setLabelCount(5, true)
                val yAxisRenderer = CustomYAxisRenderer(viewPortHandler, axisLeft, getTransformer(
                    YAxis.AxisDependency.LEFT))
                yAxisRenderer.setUnit(getString(R.string.unit_water))
                rendererLeftYAxis = yAxisRenderer
                setDrawAxisLine(false)
                axisMinimum = 0f
                axisMaximum = 2500f
                //todo maximum 계산해서 바뀔 수 있도록, 목표 물의 양 dash line으로 표시
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
        val dayAdapter = DayListAdapter()
        dataBinding.rvDayList.apply {
            setHasFixedSize(true)
            adapter = dayAdapter
            addItemDecoration(DividerDecoration(10f))
        }
        val list2 = arrayListOf(
            WaterEntity().apply {
                amount = 100
                date = "2024-03-05 01:00"
            },
            WaterEntity().apply {
                amount = 100
                date = "2024-03-05 02:00"
            },
            WaterEntity().apply {
                amount = 100
                date = "2024-03-05 05:00"
            },
            WaterEntity().apply {
                amount = 100
                date = "2024-03-05 10:00"
            },
            WaterEntity().apply {
                amount = 100
                date = "2024-03-05 12:00"
            },
            WaterEntity().apply {
                amount = 100
                date = "2024-03-05 15:00"
            },
            WaterEntity().apply {
                amount = 100
                date = "2024-03-05 17:00"
            },
            WaterEntity().apply {
                amount = 100
                date = "2024-03-05 18:00"
            },
            WaterEntity().apply {
                amount = 100
                date = "2024-03-05 19:30"
            },
            WaterEntity().apply {
                amount = 100
                date = "2024-03-05 23:10"
            },
        )
        dayAdapter.submitList(list2)

        dataBinding.tvTotalAmount.setValueAnimator(1000)
    }

    private fun initListener() {
        dataBinding.ibDayAdd.setOnClickListener {
            val dialog = LogEditBottomDialog()
            dialog.show(childFragmentManager, dialog.tag)
        }
    }
}