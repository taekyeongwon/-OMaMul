package com.tkw.alarm

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.tkw.alarm.databinding.FragmentWaterAlarmBinding
import com.tkw.alarm.dialog.AlarmRingtoneDialog
import com.tkw.alarm.dialog.ExactAlarmDialog
import com.tkw.common.NotificationManager
import com.tkw.common.PermissionHelper
import com.tkw.common.WaterAlarmManager
import com.tkw.common.autoCleared
import com.tkw.ui.CustomSwitchView
import com.tkw.ui.dialog.SettingDialog
import com.tkw.common.util.ToggleAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class WaterAlarmFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentWaterAlarmBinding>()
    private val viewModel: WaterAlarmViewModel by viewModels()

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var toolbarSwitchView: CustomSwitchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityResultLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) {

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
        initListener()
    }

    private fun initView() {
        initItemMenu()
        initAlarmModeFocusable()
        checkApi31ExactAlarm()
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

        dataBinding.tvAlarmSound.setOnClickListener {
            val soundDialog = AlarmRingtoneDialog {

            }
            soundDialog.show(childFragmentManager, soundDialog.tag)
        }
    }

    private fun checkApi31ExactAlarm() {
        if(Build.VERSION.SDK_INT >= 31 &&
            WaterAlarmManager.canScheduleExactAlarms(requireContext())) {
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
                    }
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

    private fun setSwitchButtonCheckedListener(isNotificationEnabled: Boolean) {
        toolbarSwitchView.setCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.setNotificationEnabled(isChecked)
            }
            if(isChecked) {
                if(!isNotificationEnabled) showAlert()
                else setAlarm() //알람 설정
            } else cancelAlarm() //알람 cancel
        }
    }

    private fun setSwitchButtonCheckedWithEnabled(isNotificationEnabled: Boolean) {
        lifecycleScope.launch {
            if(!isNotificationEnabled) {
                cancelAlarm()
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

    private fun setAlarm() {
        val triggerTime = Calendar.getInstance()
//                            triggerTime.set(Calendar.HOUR_OF_DAY, 22)
//                            triggerTime.set(Calendar.MINUTE, 50)
//                            triggerTime.set(Calendar.SECOND, 0)
//                            triggerTime.set(Calendar.MILLISECOND, 0)
        WaterAlarmManager.setAlarm(requireContext(), triggerTime.timeInMillis, 1000 * 60)
    }

    private fun cancelAlarm() {
        WaterAlarmManager.cancelAlarm(requireContext())
    }
}