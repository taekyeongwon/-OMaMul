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
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmModePeriodFragment : Fragment() {
    private var dataBinding by autoCleared<FragmentAlarmModePeriodBinding>()
    private val viewModel: WaterAlarmViewModel by hiltNavGraphViewModels(R.id.alarm_nav_graph)
    private var periodMode: AlarmModeSetting = AlarmModeSetting()

    private var alarmStartTime: String = ""
    private var alarmEndTime: String = ""

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
        viewModel.wakeAllAlarm()
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
            }
        }

        viewModel.tmpPeriodMode.observe(viewLifecycleOwner) {
            if(it != periodMode) {
                dataBinding.btnSave.visibility = View.VISIBLE
            } else {
                dataBinding.btnSave.visibility = View.GONE
            }
        }

        viewModel.prefSavedAlarmTime.observe(viewLifecycleOwner) {
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
    }

    private fun initListener() {
        dataBinding.alarmWeek.setCheckListListener {
            viewModel.setTmpPeriodMode(periodMode.copy(selectedDate = it))
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
                viewModel.setTmpPeriodMode(periodMode.copy(interval = interval))
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