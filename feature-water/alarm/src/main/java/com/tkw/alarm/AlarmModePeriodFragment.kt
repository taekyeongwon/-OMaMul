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
import com.tkw.common.util.toEpochMilli
import com.tkw.domain.model.AlarmModeSetting
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

    private suspend fun initNotification() {
        if(viewModel.isNotificationAlarmEnabled().first()) {
            viewModel.wakeAllAlarm()
        }
    }

    private fun initObserver() {
        viewModel.periodModeSettingsLiveData.observe(viewLifecycleOwner) {

            //해당 값으로 화면 구성
            it?.let { period ->
                periodMode = period
                viewModel.setTmpPeriodMode(period)
                dataBinding.alarmWeek.setChecked(period.selectedDate)
                dataBinding.tvIntervalSet.text = DateTimeUtils.Time.getFormat(
                    period.interval.toLong(),
                    requireContext().getString(com.tkw.ui.R.string.hour),
                    requireContext().getString(com.tkw.ui.R.string.minute)
                )
                dataBinding.tvAlarmTime.text = period.run {
                    getTimeRange(
                        DateTimeUtils.Time.getFormat(startTime),
                        DateTimeUtils.Time.getFormat(endTime)
                    )
                }
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
            val currentPeriod = DateTimeUtils.Time.getLocalTime(
                dataBinding.tvIntervalSet.text.toString(),
                requireContext().getString(com.tkw.ui.R.string.hour),
                requireContext().getString(com.tkw.ui.R.string.minute)
            )
            val dialog = AlarmPeriodDialog(currentPeriod) {
                dataBinding.tvIntervalSet.text = it
                val interval = DateTimeUtils.Time.getLocalTime(
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

    private suspend fun updateModeSetting(period: AlarmModeSetting?) {
        period?.let {
            viewModel.updateAlarmModeSetting(it)
        }
    }

    private suspend fun setAlarm(period: AlarmModeSetting) {
        viewModel.setPeriodAlarm(period)
    }

    private fun showTimeDialog() {
        viewModel.tmpPeriodMode.value?.let {
            val dialog = AlarmTimeBottomDialog(
                selectedStart = DateTimeUtils.Time.getLocalTime(it.startTime),
                selectedEnd = DateTimeUtils.Time.getLocalTime(it.endTime),
                resultListener = { wake, sleep ->
                    viewModel.tmpPeriodMode.value?.let { setting ->
                        val newSetting = setting.copy(startTime = wake.toEpochMilli(), endTime = sleep.toEpochMilli())
                        viewModel.setTmpPeriodMode(newSetting)
                        dataBinding.tvAlarmTime.text = newSetting.run {
                            getTimeRange(
                                DateTimeUtils.Time.getFormat(startTime),
                                DateTimeUtils.Time.getFormat(endTime)
                            )
                        }
                    }
                }
            )
            dialog.show(childFragmentManager, dialog.tag)
        }
    }
}