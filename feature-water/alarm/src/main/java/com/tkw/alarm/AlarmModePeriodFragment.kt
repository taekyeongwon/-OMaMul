package com.tkw.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import com.tkw.alarm.databinding.FragmentAlarmModePeriodBinding
import com.tkw.alarm.dialog.AlarmPeriodDialog
import com.tkw.alarm.dialog.AlarmTimeBottomDialog
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.domain.model.AlarmModeSetting
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime

@AndroidEntryPoint
class AlarmModePeriodFragment : Fragment() {
    private var dataBinding by autoCleared<FragmentAlarmModePeriodBinding>()
    private val viewModel: WaterAlarmViewModel by hiltNavGraphViewModels(R.id.alarm_nav_graph)
    private var periodMode: AlarmModeSetting = AlarmModeSetting()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentAlarmModePeriodBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
        initListener()
    }

    private fun initView() {
        //wake all period alarm
        lifecycleScope.launch {
            initNotification()
        }
    }

    private fun initObserver() {
        viewModel.periodModeSettingsLiveData.observe(viewLifecycleOwner) {

            //해당 값으로 화면 구성
            it?.let { period ->
                periodMode = period
                viewModel.setTmpPeriodMode(period)
                dataBinding.alarmWeek.setChecked(period.selectedDate)
                dataBinding.tvIntervalSet.text = DateTimeUtils.getTime(
                    period.interval.toLong(),
                    requireContext().getString(com.tkw.ui.R.string.hour),
                    requireContext().getString(com.tkw.ui.R.string.minute)
                )
                dataBinding.tvAlarmTime.text = period.getTimeRange()
                dataBinding.ivEdit.visibility = View.VISIBLE
            }
        }

        viewModel.tmpPeriodMode.observe(viewLifecycleOwner) {
            if(it != periodMode) {
                dataBinding.btnSave.visibility = View.VISIBLE
            } else {
                dataBinding.btnSave.visibility = View.GONE
            }
        }
    }

    private fun initListener() {
        dataBinding.alarmWeek.setCheckListListener {
            viewModel.tmpPeriodMode.value?.let { setting ->
                viewModel.setTmpPeriodMode(setting.copy(selectedDate = it))
            }
        }
        dataBinding.clPeriod.setOnClickListener {
            val currentPeriod = DateTimeUtils.getTimeFromLocalTime(
                dataBinding.tvIntervalSet.text.toString(),
                requireContext().getString(com.tkw.ui.R.string.hour),
                requireContext().getString(com.tkw.ui.R.string.minute)
            )
            val dialog = AlarmPeriodDialog(currentPeriod) {
                dataBinding.tvIntervalSet.text = it
                val interval = DateTimeUtils.getTimeFromLocalTime(
                    it,
                    requireContext().getString(com.tkw.ui.R.string.hour),
                    requireContext().getString(com.tkw.ui.R.string.minute)
                ).toSecondOfDay()
                viewModel.tmpPeriodMode.value?.let { setting ->
                    viewModel.setTmpPeriodMode(setting.copy(interval = interval))
                }
            }
            dialog.show(childFragmentManager, dialog.tag)
        }
        dataBinding.btnSave.setOnClickListener {
            viewModel.tmpPeriodMode.value?.let {
                lifecycleScope.launch {
                    updateModeSetting(it)
                    setAlarm(it)
                }
            }
        }
        dataBinding.clAlarmTimeEdit.setOnClickListener {
            showTimeDialog()
        }
    }

    private suspend fun initNotification() {
        if(viewModel.isNotificationAlarmEnabled().first()) {
            viewModel.wakeAllAlarm()
        }
    }

    private suspend fun updateModeSetting(period: AlarmModeSetting?) {
        period?.let {
            viewModel.updateAlarmModeSetting(it)
        }
    }

    private suspend fun setAlarm(period: AlarmModeSetting) {
        viewModel.setPeriodAlarm(period)
    }

    private fun showTimeDialog() {
        val dialog = AlarmTimeBottomDialog(
            selectedStart = viewModel.tmpPeriodMode.value?.startTime,
            selectedEnd = viewModel.tmpPeriodMode.value?.endTime,
            resultListener = { wake, sleep ->
                viewModel.tmpPeriodMode.value?.let { setting ->
                    val newSetting = setting.copy(startTime = wake, endTime = sleep)
                    viewModel.setTmpPeriodMode(newSetting)
                    dataBinding.tvAlarmTime.text = newSetting.getTimeRange()
                }
            }
        )
        dialog.show(childFragmentManager, dialog.tag)
    }
}