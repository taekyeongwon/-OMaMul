package com.tkw.omamul.ui.view.water.main.log

import androidx.fragment.app.viewModels
import com.google.android.material.tabs.TabLayoutMediator
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.FragmentWaterLogBinding
import com.tkw.omamul.ui.view.water.main.log.adapter.ViewPagerAdapter
import com.tkw.omamul.ui.base.BaseFragment
import com.tkw.omamul.ui.view.water.main.WaterViewModel

class WaterLogFragment: BaseFragment<FragmentWaterLogBinding, WaterViewModel>(R.layout.fragment_water_log) {
    override val viewModel: WaterViewModel by viewModels { ViewModelFactory }

    override fun initView() {
        with(dataBinding) {
            viewPager.adapter = ViewPagerAdapter(childFragmentManager, lifecycle)
            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                when(position) {
                    0 -> tab.text = getString(R.string.log_day_title)
                    1 -> tab.text = getString(R.string.log_week_title)
                    else -> tab.text = getString(R.string.log_month_title)
                }
            }.attach()
        }
    }

    override fun bindViewModel(binder: FragmentWaterLogBinding) {

    }

    override fun initObserver() {

    }

    override fun initListener() {

    }
}