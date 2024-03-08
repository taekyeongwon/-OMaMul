package com.tkw.omamul.ui.view.water.main.log

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.common.util.animateByMaxValue
import com.tkw.omamul.data.model.WaterEntity
import com.tkw.omamul.databinding.FragmentLogDayBinding
import com.tkw.omamul.ui.view.water.main.log.adapter.DayListAdapter
import com.tkw.omamul.ui.custom.DividerDecoration
import com.tkw.omamul.ui.dialog.LogEditBottomDialog
import com.tkw.omamul.ui.view.water.main.WaterViewModel
import com.tkw.omamul.common.autoCleared

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
        with(dataBinding.barChart) {
            list.add(parsingChartData(0f, 100f))
            list.add(parsingChartData(2f, 200f))
            list.add(parsingChartData(4f, 300f))
        }

        dataBinding.barChart.apply {
            setLimit(2000f) //todo 현재 설정된 목표 물의 양으로 변경 필요
            setXMinMax(0f, 24f)
            setChartData(list)
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

        dataBinding.tvTotalAmount.animateByMaxValue(1000)
    }

    private fun initListener() {
        dataBinding.ibDayAdd.setOnClickListener {
            val dialog = LogEditBottomDialog()
            dialog.show(childFragmentManager, dialog.tag)
        }
    }
}