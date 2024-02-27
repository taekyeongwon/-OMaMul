package com.tkw.omamul.ui.water.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.data.model.CupEntity
import com.tkw.omamul.databinding.FragmentWaterLogBinding
import com.tkw.omamul.ui.base.BaseFragment

class WaterLogFragment: BaseFragment<FragmentWaterLogBinding, WaterViewModel>(R.layout.fragment_water_log) {
    override val viewModel: WaterViewModel by viewModels { ViewModelFactory }

    override fun initView() {
        val list = ArrayList<BarEntry>()
        list.add(BarEntry(0f, 100f))
        list.add(BarEntry(2f, 200f))
        list.add(BarEntry(4f, 300f))
        list.add(BarEntry(6f, 400f))


        val barDataSet = BarDataSet(list, "").apply {
            color = ColorTemplate.getHoloBlue()
            valueTextColor = Color.BLACK
            valueTextSize = 16f
        }

        val barData = BarData(barDataSet)
        dataBinding.barChart.apply {
            data = barData
        }
    }

    override fun bindViewModel(binder: FragmentWaterLogBinding) {

    }

    override fun initObserver() {

    }

    override fun initListener() {

    }
}