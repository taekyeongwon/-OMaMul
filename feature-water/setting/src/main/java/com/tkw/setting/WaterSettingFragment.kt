package com.tkw.setting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withCreated
import androidx.lifecycle.withStarted
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tkw.common.autoCleared
import com.tkw.domain.Authentication
import com.tkw.home.dialog.WaterIntakeDialog
import com.tkw.navigation.DeepLinkDestination
import com.tkw.navigation.deepLinkNavigateTo
import com.tkw.setting.databinding.FragmentSettingBinding
import com.tkw.setting.dialog.LanguageDialog
import com.tkw.setting.dialog.UnitDialog
import com.tkw.ui.dialog.CustomDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WaterSettingFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentSettingBinding>()
    private val viewModel by activityViewModels<SettingViewModel>()

    @Inject
    lateinit var oAuth: Authentication

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

    override fun onStart() {
        super.onStart()
        updateUI(oAuth.isLoggedIn())
    }

    private fun initView() {
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@WaterSettingFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initListener() {
        dataBinding.settingInfo.tvSync.setOnClickListener {
            doLogin()
        }
        dataBinding.btnLogout.setOnClickListener {
            showLogoutDialog()
        }

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

    private fun updateUI(isLoggedIn: Boolean) {
        if (isLoggedIn) {
            dataBinding.settingInfo.tvSync.text = oAuth.fetchInfo()?.name ?: "-"
            dataBinding.settingInfo.tvLastSync.visibility = View.VISIBLE
            dataBinding.settingInfo.ivSync.visibility = View.VISIBLE
            dataBinding.btnLogout.visibility = View.VISIBLE
        } else {
            dataBinding.settingInfo.tvSync.text = getString(com.tkw.ui.R.string.setting_sync)
            dataBinding.settingInfo.tvLastSync.visibility = View.GONE
            dataBinding.settingInfo.ivSync.visibility = View.GONE
            dataBinding.btnLogout.visibility = View.GONE
        }
        Glide
            .with(requireContext())
            .load(oAuth.fetchInfo()?.photoUrl)
            .placeholder(com.tkw.ui.R.drawable.account_circle)
            .error(com.tkw.ui.R.drawable.account_circle)
            .fallback(com.tkw.ui.R.drawable.account_circle)
            .apply(RequestOptions().circleCrop())
            .into(dataBinding.settingInfo.ivImage)
    }

    private fun doLogin() {
        oAuth.signIn {
            if(it) {
                updateUI(true)
            }
        }
    }

    private fun showLogoutDialog() {
        val dialog = CustomDialog()
        dialog.show(childFragmentManager, dialog.tag)
        lifecycleScope.launch {
            dialog.withStarted {
                dialog.setTextView(getString(com.tkw.ui.R.string.setting_logout_confirm))
                dialog.setButtonListener(
                    confirmButtonTitle = getString(com.tkw.ui.R.string.ok),
                    cancelAction = {
                        dialog.dismiss()
                    },
                    confirmAction = {
                        dialog.dismiss()
                        oAuth.signOut()
                        updateUI(false)
                    }
                )
            }
        }
    }
}