package com.tkw.omamul.ui.view.water.main.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.data.Entry
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.common.util.animateByMaxValue
import com.tkw.omamul.databinding.FragmentLogDayBinding
import com.tkw.omamul.ui.view.water.main.log.adapter.DayListAdapter
import com.tkw.omamul.ui.custom.DividerDecoration
import com.tkw.omamul.ui.dialog.LogEditBottomDialog
import com.tkw.omamul.ui.view.water.main.WaterViewModel
import com.tkw.omamul.common.autoCleared
import com.tkw.omamul.data.model.WaterEntity
import com.tkw.omamul.ui.custom.CustomMarkerView

class LogDayFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentLogDayBinding>()
    private val viewModel: WaterViewModel by activityViewModels { ViewModelFactory }
    private val dayAdapter = DayListAdapter()

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
        dataBinding.rvDayList.run {
            setHasFixedSize(true)
            adapter = dayAdapter
            addItemDecoration(DividerDecoration(10f))
        }

    }

    private fun initObserver() {
        viewModel.countStreamLiveData.observe(viewLifecycleOwner) { data ->
            with(dataBinding) {
                val result = data.getAccumulatedAmount().map {
                    barChart.parsingChartData(it.key, it.value)
                }
                barChart.setLimit(2000f) //todo 현재 설정된 목표 물의 양으로 변경 필요
                barChart.setUnit(getString(R.string.unit_hour), getString(R.string.unit_ml))
                barChart.setMarker(CustomMarkerView(requireContext(), R.layout.custom_marker, requireContext().getString(R.string.unit_ml)))
                barChart.setChartData(result)
                tvTotalAmount.animateByMaxValue(result.last().y.toInt())
            }
            dayAdapter.submitList(data.dayOfList)
        }
    }

    private fun initListener() {
        dataBinding.ibDayAdd.setOnClickListener {
            val dialog = LogEditBottomDialog()
            dialog.show(childFragmentManager, dialog.tag)
        }
    }
}