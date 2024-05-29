package com.tkw.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.tkw.alarm.databinding.FragmentWaterAlarmBinding
import com.tkw.common.autoCleared
import com.tkw.ui.CustomSwitchView
import com.tkw.ui.util.ToggleAnimation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaterAlarmFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentWaterAlarmBinding>()
    private val viewModel: WaterAlarmViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentWaterAlarmBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        initItemMenu()
        initAlarmModeFocusable()
    }

    private fun initListener() {
        dataBinding.tvAlarmModePeriod.setFocusChangeListener({
            ToggleAnimation.expand(dataBinding.alarmPeriodLayout)
        }, {
            ToggleAnimation.collapse(dataBinding.alarmPeriodLayout)
        })
        dataBinding.tvAlarmModeCustom.setFocusChangeListener({
            ToggleAnimation.expand(dataBinding.alarmCustomLayout)
        }, {
            ToggleAnimation.collapse(dataBinding.alarmCustomLayout)
        })
        dataBinding.tvAlarmModePeriod.setSelected() //todo 저장된 모드 setSelected 호출 필요
    }

    private fun initItemMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_toggle, menu)
                val toggleItem = menu.findItem(R.id.alarm_toggle)
                val customView = toggleItem.actionView as CustomSwitchView


            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun initAlarmModeFocusable() {
        dataBinding.tvAlarmModePeriod.setFocusable()
        dataBinding.tvAlarmModeCustom.setFocusable()
    }
}