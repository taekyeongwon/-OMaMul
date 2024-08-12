package com.tkw.alarm

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
import androidx.fragment.app.commit
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.tkw.alarm.databinding.FragmentAlarmModeBinding
import com.tkw.alarm.dialog.AlarmModeBottomDialog
import com.tkw.alarm.dialog.ExactAlarmDialog
import com.tkw.alarmnoti.NotificationManager
import com.tkw.common.PermissionHelper
import com.tkw.common.autoCleared
import com.tkw.domain.IAlarmManager
import com.tkw.domain.model.AlarmMode
import com.tkw.ui.CustomSwitchView
import com.tkw.ui.dialog.SettingDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmModeFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentAlarmModeBinding>()
    private val viewModel: WaterAlarmViewModel by hiltNavGraphViewModels(R.id.alarm_nav_graph)
    private var currentMode: AlarmMode? = null
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var toolbarSwitchView: CustomSwitchView

    @Inject
    lateinit var alarmManager: IAlarmManager

    private val fragmentList by lazy {
        listOf(
            AlarmModePeriodFragment(),
            AlarmModeCustomFragment()
        )
    }

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
        dataBinding = FragmentAlarmModeBinding.inflate(inflater, container, false)
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
        dataBinding.run {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@AlarmModeFragment.viewModel
            executePendingBindings()
        }
    }

    private fun initObserver() {
        viewModel.alarmMode.observe(viewLifecycleOwner) {
            //현재 뷰모델 setting에서 가져온 모드로 replace
            it?.let {
                setAlarmModeText(it)
                currentMode = it

                when(it) {
                    AlarmMode.PERIOD -> {
                        replaceFragment(fragmentList[0])
                    }
                    AlarmMode.CUSTOM -> {
                        replaceFragment(fragmentList[1])
                    }
                }
            }
        }
    }

    private fun initListener() {
        dataBinding.tvAlarmMode.setOnClickListener {
            val dialog = AlarmModeBottomDialog(currentMode!!) {
                viewModel.updateAlarmMode(it)
            }
            dialog.show(childFragmentManager, dialog.tag)
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
                        lifecycleScope.launch {
                            viewModel.setNotificationEnabled(it)
                            setSwitchButtonCheckedWithEnabled(it)
                            setSwitchButtonCheckedListener(it)
                        }
//                        checkApi31ExactAlarm()
                    }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private suspend fun setSwitchButtonCheckedWithEnabled(isNotificationEnabled: Boolean) {
        val isEnabled = isNotificationEnabled && viewModel.getNotificationEnabled()
        if(isEnabled) {
            alarmOn()
        } else {
            alarmOff()
        }
        toolbarSwitchView.setChecked(isEnabled)
        viewModel.setAlarmEnabled(isEnabled)
    }

    private fun setSwitchButtonCheckedListener(isNotificationEnabled: Boolean) {
        toolbarSwitchView.setCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                viewModel.setAlarmEnabled(isChecked)
            }
            if(isChecked) {
                if(!isNotificationEnabled) showAlert()
                else alarmOn() //알람 설정
            } else alarmOff() //알람 cancel
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.commit {
            replace(dataBinding.container.id, fragment, fragment.tag)
        }
    }

    private fun setAlarmModeText(mode: AlarmMode) {
        val text = when(mode) {
            AlarmMode.PERIOD -> getString(com.tkw.ui.R.string.alarm_mode_period)
            AlarmMode.CUSTOM -> getString(com.tkw.ui.R.string.alarm_mode_custom)
        }
        dataBinding.tvAlarmMode.setText(text)
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
        viewModel.sleepAllAlarm()
    }
}