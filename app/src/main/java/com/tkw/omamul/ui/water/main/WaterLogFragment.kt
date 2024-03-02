package com.tkw.omamul.ui.water.main

import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.FragmentWaterLogBinding
import com.tkw.omamul.ui.adapter.ViewPagerAdapter
import com.tkw.omamul.ui.base.BaseFragment
import com.tkw.omamul.ui.custom.CustomMarkerView
import com.tkw.omamul.ui.custom.CustomYAxisRenderer
import com.tkw.omamul.ui.custom.XAxisValueFormatter
import java.text.DecimalFormat

class WaterLogFragment: BaseFragment<FragmentWaterLogBinding, WaterViewModel>(R.layout.fragment_water_log) {
    override val viewModel: WaterViewModel by viewModels { ViewModelFactory }

    override fun initView() {
        with(dataBinding) {
            viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
            TabLayoutMediator(tabLayout, viewPager, object: TabConfigurationStrategy {
                override fun onConfigureTab(tab: TabLayout.Tab, position: Int) {
                    tab.text = ""+position
                }
            }).attach()
        }
    }

    override fun bindViewModel(binder: FragmentWaterLogBinding) {

    }

    override fun initObserver() {

    }

    override fun initListener() {

    }
}