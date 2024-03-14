package com.tkw.omamul.ui.view.water.main.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.BarEntry
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.FragmentLogWeekBinding
import com.tkw.omamul.ui.custom.chart.DayMarkerView
import com.tkw.omamul.ui.view.water.main.WaterViewModel
import com.tkw.omamul.common.autoCleared
import com.tkw.omamul.ui.custom.chart.WeekMarkerView

class LogWeekFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentLogWeekBinding>()
    private val viewModel: WaterViewModel by viewModels { ViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentLogWeekBinding.inflate(inflater, container, false)
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
            viewModel = this@LogWeekFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initView() {
        val list = ArrayList<BarEntry>()
        with(dataBinding.barChart) {
            list.add(parsingChartData(1f, 0.1f))
            list.add(parsingChartData(2f, 0.2f))
            list.add(parsingChartData(3f, 0.3f))
            list.add(parsingChartData(7f, 0.4f))
            setLimit(2f) //todo 현재 설정된 목표 물의 양으로 변경 필요
            setUnit(getString(R.string.unit_day), getString(R.string.unit_liter))
            setMarker(WeekMarkerView(context, R.layout.custom_marker))
            setChartData(list)
        }
    }
}