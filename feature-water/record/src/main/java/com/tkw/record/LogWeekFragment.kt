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
import com.tkw.common.util.DateTimeUtils
import com.tkw.common.util.DateTimeUtil
import com.tkw.common.util.animateByMaxValue
import com.tkw.domain.model.DayOfWaterList
import com.tkw.record.databinding.FragmentLogWeekBinding
import com.tkw.ui.chart.marker.MarkerType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter

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
        viewLifecycleOwner.lifecycleScope.launch {
            initChart()
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
                            val dayOfWaterList = it.data
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

    private suspend fun initChart() {
        val amount = viewModel.getIntakeAmount()
        with(dataBinding) {
            barChart.setLimit(amount / 1000)
            barChart.setYUnit(getString(com.tkw.ui.R.string.unit_liter))
            barChart.setMarker(MarkerType.WEEK)
            barChart.setXMinMax(1f, 7f)
        }
    }

    private fun setChartData(dayOfWaterList: DayOfWaterList) {
        val list = dayOfWaterList.list
        with(dataBinding) {
            val result = list.map {
                barChart.parsingChartData(
                    DateTimeUtil.getIndexOfWeek(it.date).toFloat(),
                    it.getTotalIntakeByDate().toFloat() / 1000
                )
            }
            if(list.isNotEmpty()) {
                barChart.setXValueFormat(getWeekDateList(list[0].date))
            }

            barChart.setChartData(result)
            tvTotalAmount.animateByMaxValue(dayOfWaterList.getTotalIntake() / 1000f)
        }
    }

    private fun getWeekDateList(date: String): Array<String> {
        val formatter = DateTimeFormatter.ofPattern("MM/dd")
        val localDate = DateTimeUtils.Date.getLocalDate(date)
        val week = ArrayList<String>()
        for(i in 1..7) {
            week.add(localDate.with(DayOfWeek.of(i)).format(formatter))
        }
        return week.toArray(arrayOf())
    }
}