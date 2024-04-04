package com.tkw.omamul.ui.view.water.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.data.BarEntry
import com.tkw.omamul.R
import com.tkw.omamul.common.getViewModelFactory
import com.tkw.omamul.databinding.FragmentLogMonthBinding
import com.tkw.omamul.ui.view.water.home.WaterViewModel
import com.tkw.omamul.common.autoCleared
import com.tkw.omamul.ui.custom.chart.MonthMarkerView

class LogMonthFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentLogMonthBinding>()
    private val viewModel: LogViewModel by activityViewModels { getViewModelFactory(null) }

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
        with(dataBinding.barChart) {
            list.add(parsingChartData(1f, 0.3f))
            list.add(parsingChartData(5f, 0.3f))
            list.add(parsingChartData(16f, 0.4f))
            list.add((parsingChartData(31f, 0f)))
            setLimit(2f)
            setUnit(getString(R.string.unit_day), getString(R.string.unit_liter))
            setMarker(MonthMarkerView(context, R.layout.custom_marker_month))
            setChartData(list)
        }
    }
}