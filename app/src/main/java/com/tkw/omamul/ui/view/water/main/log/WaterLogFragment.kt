package com.tkw.omamul.ui.view.water.main.log

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.tkw.omamul.R
import com.tkw.omamul.databinding.FragmentWaterLogBinding
import com.tkw.omamul.ui.view.water.main.log.adapter.ViewPagerAdapter
import com.tkw.omamul.common.autoCleared

class WaterLogFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentWaterLogBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentWaterLogBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
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
}