package com.tkw.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import com.tkw.alarm.databinding.FragmentAlarmModeBinding
import com.tkw.alarm.dialog.AlarmModeBottomDialog
import com.tkw.alarm.dialog.AlarmTimeBottomDialog
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmModeSetting
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmModeFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentAlarmModeBinding>()
    private val viewModel: WaterAlarmViewModel by hiltNavGraphViewModels(R.id.alarm_nav_graph)
    private var alarmStartTime: String = ""
    private var alarmEndTime: String = ""
    private var currentMode: AlarmMode? = null

    private val fragmentList by lazy {
        listOf(
            AlarmModePeriodFragment(),
            AlarmModeCustomFragment()
        )
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

        viewModel.alarmTime.observe(viewLifecycleOwner) {
            val first = it?.first
            val second = it?.second
            if(first != null && second != null) {
                alarmStartTime = DateTimeUtils.getFormattedTime(first.hour, first.minute)
                alarmEndTime = DateTimeUtils.getFormattedTime(second.hour, second.minute)
                dataBinding.tvAlarmTime.text = "$alarmStartTime - $alarmEndTime"
                dataBinding.ivEdit.visibility = View.VISIBLE
            } else {
                dataBinding.ivEdit.visibility = View.GONE
            }
        }

//        viewModel.alarmModeSettingsLiveData.observe(viewLifecycleOwner) {
//            it?.let {
//                when(it) {
//                    is AlarmModeSetting.Period -> {
//                        //현재 설정된 알람 몇 분 남았는지 세팅
//                    }
//                    is AlarmModeSetting.Custom -> {
//                        //현재 설정된 알람 몇 분 남았는지 세팅
//                    }
//                }
//            }
//        }
    }

    private fun initListener() {
        dataBinding.tvAlarmMode.setOnClickListener {
            val dialog = AlarmModeBottomDialog(currentMode!!) {
                viewModel.updateAlarmMode(it)
            }
            dialog.show(childFragmentManager, dialog.tag)
        }

        dataBinding.clAlarmTimeEdit.setOnClickListener {
            showTimeDialog()
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

    private fun showTimeDialog() {
        val dialog = AlarmTimeBottomDialog(
            selectedStart = DateTimeUtils.getTimeFromFormat(alarmStartTime),
            selectedEnd = DateTimeUtils.getTimeFromFormat(alarmEndTime),
            resultListener = { wake, sleep ->
                lifecycleScope.launch {
                    viewModel.setAlarmTime(
                        DateTimeUtils.getFormattedTime(wake.hour, wake.minute),
                        DateTimeUtils.getFormattedTime(sleep!!.hour, sleep.minute)
                    )
                }
            }
        )
        dialog.show(childFragmentManager, dialog.tag)
    }
}