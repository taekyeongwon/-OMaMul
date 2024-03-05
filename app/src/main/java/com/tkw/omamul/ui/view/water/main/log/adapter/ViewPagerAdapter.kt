package com.tkw.omamul.ui.view.water.main.log.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tkw.omamul.ui.view.water.main.log.LogDayFragment
import com.tkw.omamul.ui.view.water.main.log.LogMonthFragment
import com.tkw.omamul.ui.view.water.main.log.LogWeekFragment

class ViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle)
    : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> LogDayFragment()
            1 -> LogWeekFragment()
            else -> LogMonthFragment()
        }
    }
}