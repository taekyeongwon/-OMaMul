package com.tkw.omamul.ui.view.water.log

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
import com.tkw.omamul.R
import com.tkw.omamul.common.getViewModelFactory
import com.tkw.omamul.common.util.animateByMaxValue
import com.tkw.omamul.databinding.FragmentLogDayBinding
import com.tkw.omamul.ui.view.water.log.adapter.DayListAdapter
import com.tkw.omamul.ui.custom.DividerDecoration
import com.tkw.omamul.ui.dialog.LogEditBottomDialog
import com.tkw.omamul.common.autoCleared
import com.tkw.omamul.data.model.DayOfWater
import com.tkw.omamul.data.model.Water
import com.tkw.omamul.ui.custom.chart.DayMarkerView
import kotlinx.coroutines.launch

class LogDayFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentLogDayBinding>()
    private val viewModel: LogViewModel by activityViewModels { getViewModelFactory(null) }
    private lateinit var dayAdapter: DayListAdapter

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
        initObserver()
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
        dayAdapter = DayListAdapter(dayAmountEditListener, dayAmountDeleteListener)
        dataBinding.rvDayList.apply {
            adapter = dayAdapter
            addItemDecoration(DividerDecoration(10f))
        }
    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(State.RESUMED) {
                viewModel.state.collect {
                    when(it) {
                        is LogContract.State.Complete -> {
                            val dayOfWater = it.data.list[0]
                            setChartData(dayOfWater)
                            dayAdapter.submitList(dayOfWater.dayOfList) {
                                dataChanged()
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

        lifecycleScope.launch {
            viewModel.sideEffect.collect {
                when(it) {
                    is LogContract.SideEffect.ShowEditDialog -> {
                        if(it.water != null) {
                            val dialog = LogEditBottomDialog(it.water)
                            dialog.show(childFragmentManager, dialog.tag)
                        } else {
                            val dialog = LogEditBottomDialog()
                            dialog.show(childFragmentManager, dialog.tag)
                        }
                    }
                }
            }
        }
    }

    private fun initListener() {
        dataBinding.ibDayAdd.setOnClickListener {
            val dialog = LogEditBottomDialog()
            dialog.show(childFragmentManager, dialog.tag)
            viewModel.setEvent(LogContract.Event.ShowAddDialog)
        }

        dataBinding.ibDayLeft.setOnClickListener {
            viewModel.setEvent(LogContract.Event.GetDayAmount(LogContract.Move.LEFT))
        }

        dataBinding.ibDayRight.setOnClickListener {
            viewModel.setEvent(LogContract.Event.GetDayAmount(LogContract.Move.RIGHT))
        }
    }

    private val dayAmountEditListener: (Int) -> Unit = { position ->
        val item: Water = dayAdapter.currentList[position]
        viewModel.setEvent(LogContract.Event.ShowEditDialog(item))
    }

    private val dayAmountDeleteListener: (Int) -> Unit = { position ->
        val item: Water = dayAdapter.currentList[position]
        viewModel.setEvent(LogContract.Event.RemoveDayAmount(item))
    }

    private fun setChartData(dayOfWater: DayOfWater) {
        with(dataBinding) {
            val result = dayOfWater.getAccumulatedAmount().map {
                barChart.parsingChartData(it.key, it.value)
            }
            barChart.setLimit(2000f) //todo 현재 설정된 목표 물의 양으로 변경 필요
            barChart.setUnit(getString(R.string.unit_hour), getString(R.string.unit_ml))
            barChart.setMarker(DayMarkerView(requireContext(), R.layout.custom_marker))
            barChart.setChartData(result)
            tvTotalAmount.animateByMaxValue(result.lastOrNull()?.y?.toInt() ?: 0)
        }
    }

    private fun dataChanged() {
        if(dayAdapter.itemCount == 0) {
            dataBinding.nvEmptyView.visibility = View.VISIBLE
            dataBinding.rvDayList.visibility = View.GONE
        } else {
            dataBinding.nvEmptyView.visibility = View.GONE
            dataBinding.rvDayList.visibility = View.VISIBLE
        }
    }
}