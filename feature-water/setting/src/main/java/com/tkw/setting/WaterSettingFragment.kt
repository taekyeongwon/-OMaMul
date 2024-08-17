package com.tkw.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tkw.common.autoCleared
import com.tkw.home.dialog.WaterIntakeDialog
import com.tkw.navigation.DeepLinkDestination
import com.tkw.navigation.deepLinkNavigateTo
import com.tkw.setting.databinding.FragmentSettingBinding
import com.tkw.setting.dialog.LanguageDialog
import com.tkw.setting.dialog.UnitDialog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class WaterSettingFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentSettingBinding>()
    private val viewModel by activityViewModels<SettingViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentSettingBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initView() {
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@WaterSettingFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initListener() {
        dataBinding.settingWater.clWaterSettingIntake.setOnClickListener {
            val dialog = WaterIntakeDialog()
            dialog.show(childFragmentManager, dialog.tag)
        }
        dataBinding.settingWater.clWaterSettingCup.setOnClickListener {
            findNavController().deepLinkNavigateTo(requireContext(), DeepLinkDestination.Cup)
        }
        dataBinding.settingWater.clWaterSettingUnit.setOnClickListener {
            lifecycleScope.launch {
                val dialog = UnitDialog(viewModel.unitFlow.firstOrNull() ?: 0)
                dialog.show(childFragmentManager, dialog.tag)
            }

        }

        dataBinding.settingAlarm.clAlarmSetting.setOnClickListener {
            findNavController().deepLinkNavigateTo(requireContext(), DeepLinkDestination.Alarm)
        }

        dataBinding.settingEtc.clEtcSettingLanguage.setOnClickListener {
            lifecycleScope.launch {
                val dialog = LanguageDialog(viewModel.currentLangFlow.firstOrNull() ?: "")
                dialog.show(childFragmentManager, dialog.tag)
            }
        }
    }
}