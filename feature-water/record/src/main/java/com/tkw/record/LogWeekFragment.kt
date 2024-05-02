package com.tkw.record

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tkw.common.autoCleared
import com.tkw.util.animateByMaxValue
import com.tkw.domain.model.DayOfWater
import com.tkw.record.databinding.FragmentLogWeekBinding
import com.tkw.ui.chart.WeekMarkerView
import com.tkw.ui.chart.XAxisWeekFormatter
import com.tkw.util.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogWeekFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentLogWeekBinding>()
    private val viewModel: LogViewModel by activityViewModels()

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
        initObserver()
        initListener()
    }

    private fun initBinding() {
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@LogWeekFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initView() {
        initChart()
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.setEvent(LogContract.Event.WeekAmountEvent(LogContract.Move.INIT))
            }
        }
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect {
                when(it) {
                    is LogContract.State.Complete -> {
                        if(it.unit == LogContract.DateUnit.WEEK) {
                            val dayOfWaterList = it.data.list
                            setChartData(dayOfWaterList)
                        }
                    }
                    LogContract.State.Error -> {
                        Log.d("LogDayFragment", "error")
                    }
                    is LogContract.State.Loading -> {
                        Log.d("LogDayFragment", "onProgress ${it.flag}")
                    }
                }
            }
        }
    }

    private fun initListener() {
        dataBinding.ibDayLeft.setOnClickListener {
            viewModel.setEvent(LogContract.Event.WeekAmountEvent(LogContract.Move.LEFT))
        }

        dataBinding.ibDayRight.setOnClickListener {
            viewModel.setEvent(LogContract.Event.WeekAmountEvent(LogContract.Move.RIGHT))
        }
    }

    private fun initChart() {
        with(dataBinding) {
            barChart.setLimit(2f) //todo 현재 설정된 목표 물의 양으로 변경 필요
            barChart.setUnit(getString(com.tkw.base.R.string.unit_liter))
            barChart.setMarker(WeekMarkerView(context))
            barChart.setXMinMax(1f, 7f)
        }
    }

    private fun setChartData(list: List<DayOfWater>) {
        with(dataBinding) {
            val result = list.map {
                barChart.parsingChartData(
                    DateTimeUtils.getIndexOfWeek(it.date).toFloat(),
                    it.getTotalWaterAmount().toFloat() / 1000
                )
            }
            if(list.isNotEmpty()) {
                barChart.setXAxisValueFormatter(XAxisWeekFormatter(list[0].date))
            }

            barChart.setChartData(result)
            tvTotalAmount.animateByMaxValue(list.sumOf { it.getTotalWaterAmount() } / 1000f)
        }
    }
}