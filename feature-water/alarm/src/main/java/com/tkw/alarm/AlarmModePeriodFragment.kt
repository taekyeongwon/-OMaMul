package com.tkw.alarm

import android.os.Bundle
import android.util.Log
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
import com.tkw.common.util.DateTimeUtils.toEpochMilli
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmModeSetting
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.DayOfWeek

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
        viewModel.wakeAllAlarm()
    }

    private fun initObserver() {
        viewModel.periodModeSettingsLiveData.observe(viewLifecycleOwner) {

            //해당 값으로 화면 구성
            it?.let { period ->
                periodMode = period
                dataBinding.alarmWeek.setChecked(period.selectedDate)
                dataBinding.alarmWeek.setPeriodTime(
                    DateTimeUtils.getTime(
                        period.interval.toLong(),
                        requireContext().getString(com.tkw.ui.R.string.hour),
                        requireContext().getString(com.tkw.ui.R.string.minute)
                    )
                )
                setAlarm(period)
            }
        }
    }

    private fun initListener() {
        dataBinding.alarmWeek.setCheckListListener {
            updateModeSetting(
                periodMode.copy(selectedDate = it)
            )
        }
        dataBinding.alarmWeek.setPeriodClickListener {
            val currentPeriod = DateTimeUtils.getTimeFromLocalTime(
                dataBinding.alarmWeek.getPeriodTime(),
                requireContext().getString(com.tkw.ui.R.string.hour),
                requireContext().getString(com.tkw.ui.R.string.minute)
            )
            val dialog = AlarmPeriodDialog(currentPeriod) {
                dataBinding.alarmWeek.setPeriodTime(it)
                val interval = DateTimeUtils.getTimeFromLocalTime(
                    it,
                    requireContext().getString(com.tkw.ui.R.string.hour),
                    requireContext().getString(com.tkw.ui.R.string.minute)
                ).toSecondOfDay()
                updateModeSetting(
                    periodMode.copy(interval = interval)
                )
            }
            dialog.show(childFragmentManager, dialog.tag)
        }

    }

    private fun updateModeSetting(period: AlarmModeSetting?) {
        period?.let {
            viewModel.updateAlarmModeSetting(it)
        }
    }

    private fun setAlarm(period: AlarmModeSetting) {
        lifecycleScope.launch {
            viewModel.setPeriodAlarm(period)
        }
    }
}