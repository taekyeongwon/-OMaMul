package com.tkw.setting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.tkw.common.autoCleared
import com.tkw.setting.databinding.FragmentSettingBinding

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
    }

    private fun initView() {
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@WaterSettingFragment.viewModel
            executePendingBindings()
        }
    }
}