package com.tkw.record

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tkw.common.autoCleared
import com.tkw.ui.util.animateByMaxValue
import com.tkw.domain.model.DayOfWater
import com.tkw.record.databinding.FragmentLogMonthBinding
import com.tkw.ui.chart.marker.MarkerType
import com.tkw.ui.util.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LogMonthFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentLogMonthBinding>()
    private val viewModel: LogViewModel by activityViewModels()

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
        initObserver()
        initListener()
    }

    private fun initBinding() {
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@LogMonthFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initView() {
        viewLifecycleOwner.lifecycleScope.launch {
            initChart()
            repeatOnLifecycle(State.RESUMED) {
                viewModel.setEvent(LogContract.Event.MonthAmountEvent(LogContract.Move.INIT))
            }
        }
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect {
                when(it) {
                    is LogContract.State.Complete -> {
                        if(it.unit == LogContract.DateUnit.MONTH) {
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
            viewModel.setEvent(LogContract.Event.MonthAmountEvent(LogContract.Move.LEFT))
        }

        dataBinding.ibDayRight.setOnClickListener {
            viewModel.setEvent(LogContract.Event.MonthAmountEvent(LogContract.Move.RIGHT))
        }
    }

    private suspend fun initChart() {
        val amount = viewModel.getIntakeAmount()
        with(dataBinding) {
            barChart.setLimit(amount / 1000)
            barChart.setUnit(
                getString(com.tkw.ui.R.string.unit_day),
                getString(com.tkw.ui.R.string.unit_liter)
            )
            barChart.setMarker(MarkerType.MONTH)
        }
    }

    private fun setChartData(list: List<DayOfWater>) {
        with(dataBinding) {
            val result = list.map {
                barChart.parsingChartData(
                    it.date.split("-").last().toFloat(),
                    it.getTotalWaterAmount().toFloat() / 1000
                )
            }
            if(list.isNotEmpty()) {
                val month = DateTimeUtils.getMonthDates(list[0].date)
                barChart.setXMinMax(
                    month.first.split("-").last().toFloat(),
                    month.second.split("-").last().toFloat()
                )
            }

            barChart.setChartData(result)
            tvTotalAmount.animateByMaxValue(list.sumOf { it.getTotalWaterAmount() } / 1000f)
        }
    }
}