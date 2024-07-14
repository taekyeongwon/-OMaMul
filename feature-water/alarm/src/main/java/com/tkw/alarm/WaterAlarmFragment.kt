package com.tkw.alarm

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tkw.alarm.databinding.FragmentWaterAlarmBinding
import com.tkw.alarm.dialog.AlarmRingtoneDialog
import com.tkw.alarm.dialog.ExactAlarmDialog
import com.tkw.alarmnoti.NotificationManager
import com.tkw.common.PermissionHelper
import com.tkw.common.autoCleared
import com.tkw.domain.IAlarmManager
import com.tkw.domain.model.AlarmEtcSettings
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.RingTone
import com.tkw.ui.CustomSwitchView
import com.tkw.ui.dialog.SettingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WaterAlarmFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentWaterAlarmBinding>()
    private val viewModel: WaterAlarmViewModel by hiltNavGraphViewModels(R.id.alarm_nav_graph)
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var toolbarSwitchView: CustomSwitchView

    @Inject
    lateinit var alarmManager: IAlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {
                //MenuProvider onCreateMenu가 resume마다 다시 호출되므로 아무 것도 처리하지 않음.
            }
    }

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
        initObserver()
        initListener()
    }

    private fun initView() {
        initItemMenu()
    }

    private fun initObserver() {
        viewModel.alarmSettings.observe(viewLifecycleOwner) {
            //가져온 데이터로 화면 구성
            setRingtoneTitle(it.ringToneMode.getCurrentMode())
            setAlarmModeTitle(it.alarmMode)
            setEtcSetting(it.etcSetting)
        }
    }

    private fun initListener() {
        dataBinding.tvAlarmSound.setOnClickListener {
            val soundDialog = AlarmRingtoneDialog {

            }
            soundDialog.show(childFragmentManager, soundDialog.tag)
        }

        dataBinding.clAlarmMode.setOnClickListener {
            findNavController().navigate(WaterAlarmFragmentDirections
                .actionWaterAlarmFragmentToAlarmModeFragment())
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            dataBinding.tvFullscreenSetting.setOnClickListener {
                val intent = Intent(
                    Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT,
                    Uri.parse("package:${requireContext().packageName}")
                )
                startActivity(intent)
            }
        } else {
            dataBinding.divider2.visibility = View.GONE
            dataBinding.tvFullscreenSetting.visibility = View.GONE
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dataBinding.tvExactSetting.setOnClickListener {
                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                    Uri.parse("package:${requireContext().packageName}")
                )
                startActivity(intent)
            }
        } else {
            dataBinding.divider3.visibility = View.GONE
            dataBinding.tvExactSetting.visibility = View.GONE
        }
    }

    private fun checkApi31ExactAlarm() {
        if(Build.VERSION.SDK_INT >= 31 &&
            !alarmManager.canScheduleExactAlarms()) {
            if(toolbarSwitchView.getChecked() /* && 다시 보지 않기 체크 여부 */) {
                val dialog = ExactAlarmDialog()
                dialog.show(childFragmentManager, dialog.tag)
            }
        }
    }

    private fun initItemMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_toggle, menu)
                val toggleItem = menu.findItem(R.id.alarm_toggle)
                toolbarSwitchView = toggleItem.actionView!!.findViewById(R.id.toolbar_switch)

                NotificationManager.isNotificationEnabled(requireContext())
                    .also {
                        setSwitchButtonCheckedListener(it)
                        setSwitchButtonCheckedWithEnabled(it)
                        checkApi31ExactAlarm()
                    }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setSwitchButtonCheckedListener(isNotificationEnabled: Boolean) {
        toolbarSwitchView.setCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.setNotificationEnabled(isChecked)
            }
            if(isChecked) {
                if(!isNotificationEnabled) showAlert()
                else alarmOn() //알람 설정
            } else alarmOff() //알람 cancel
        }
    }

    private fun setSwitchButtonCheckedWithEnabled(isNotificationEnabled: Boolean) {
        lifecycleScope.launch {
            if(!isNotificationEnabled) {
                alarmOff()
            }
            toolbarSwitchView.setChecked(
                isNotificationEnabled && viewModel.getNotificationEnabled()
            )
        }
    }

    private fun showAlert() {
        val dialog = SettingDialog(
            cancelAction = {
                toolbarSwitchView.setChecked(false)
            },
            confirmAction = {
                PermissionHelper.goToNotificationSetting(
                    requireActivity(),
                    activityResultLauncher
                )
            }
        )
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun alarmOn() {
        viewModel.wakeAllAlarm()
    }

    private fun alarmOff() {
        viewModel.cancelAllAlarm()
    }

    private fun setRingtoneTitle(ringtone: RingTone) {
        val soundTitle = when(ringtone) {
            RingTone.DEVICE -> getString(com.tkw.ui.R.string.alarm_sound_device)
            RingTone.BELL -> getString(com.tkw.ui.R.string.alarm_sound_ringtone)
            RingTone.VIBE -> getString(com.tkw.ui.R.string.alarm_sound_vibe)
            RingTone.ALL -> getString(com.tkw.ui.R.string.alarm_sound_all)
            RingTone.IGNORE -> getString(com.tkw.ui.R.string.alarm_sound_silence)
        }
        dataBinding.tvAlarmSound.setText(soundTitle)
    }

    private fun setAlarmModeTitle(alarmMode: AlarmMode) {
        val modeTitle = when(alarmMode) {
            AlarmMode.PERIOD -> getString(com.tkw.ui.R.string.alarm_mode_period)
            AlarmMode.CUSTOM -> getString(com.tkw.ui.R.string.alarm_mode_custom)
        }
        dataBinding.tvAlarmMode.setText(modeTitle)
    }

    private fun setEtcSetting(etcSettings: AlarmEtcSettings) {
        dataBinding.tvAlarmEtcStop.setSwitch(etcSettings.stopReachedGoal) { _, isChecekd ->
            //todo 목표 도달 시 알람 멈추기 기능 구현 필요
        }
    }
}