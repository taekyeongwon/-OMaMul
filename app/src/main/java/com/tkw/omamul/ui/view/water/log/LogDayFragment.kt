package com.tkw.omamul.ui.view.water.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.tkw.omamul.R
import com.tkw.omamul.common.getViewModelFactory
import com.tkw.omamul.common.util.animateByMaxValue
import com.tkw.omamul.databinding.FragmentLogDayBinding
import com.tkw.omamul.ui.view.water.log.adapter.DayListAdapter
import com.tkw.omamul.ui.custom.DividerDecoration
import com.tkw.omamul.ui.dialog.LogEditBottomDialog
import com.tkw.omamul.ui.view.water.WaterViewModel
import com.tkw.omamul.common.autoCleared
import com.tkw.omamul.data.model.Water
import com.tkw.omamul.ui.custom.chart.DayMarkerView

class LogDayFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentLogDayBinding>()
    private val viewModel: WaterViewModel by activityViewModels { getViewModelFactory(null) }
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
        dayAdapter.registerAdapterDataObserver(emptyRecyclerObserver)
        dataBinding.rvDayList.apply {
            adapter = dayAdapter
            addItemDecoration(DividerDecoration(10f))
        }
    }

    private fun initObserver() {
        viewModel.amountLiveData.observe(viewLifecycleOwner) { dayOfWater ->
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
            dayAdapter.submitList(dayOfWater.dayOfList)
            emptyRecyclerObserver.onChanged()
        }
    }

    private fun initListener() {
        dataBinding.ibDayAdd.setOnClickListener {
            val dialog = LogEditBottomDialog()
            dialog.show(childFragmentManager, dialog.tag)
        }
    }

    private val dayAmountEditListener: (Int) -> Unit = { position ->
        val item: Water = dayAdapter.currentList[position]
        val dialog = LogEditBottomDialog(item)
        dialog.show(childFragmentManager, dialog.tag)
    }

    private val dayAmountDeleteListener: (Int) -> Unit = { position ->
        val item: Water = dayAdapter.currentList[position]
        viewModel.removeCount(item)
    }

    private val emptyRecyclerObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            if(dayAdapter.itemCount == 0) {
                dataBinding.nvEmptyView.visibility = View.VISIBLE
                dataBinding.rvDayList.visibility = View.GONE
            } else {
                dataBinding.nvEmptyView.visibility = View.GONE
                dataBinding.rvDayList.visibility = View.VISIBLE
            }
        }
    }
}