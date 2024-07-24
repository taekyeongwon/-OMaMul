package com.tkw.alarm

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withResumed
import androidx.navigation.navGraphViewModels
import com.tkw.alarm.databinding.FragmentAlarmModePeriodBinding
import com.tkw.alarm.dialog.AlarmPeriodDialog
import com.tkw.alarm.dialog.AlarmTimeBottomDialog
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmModeSetting
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalTime

@AndroidEntryPoint
class AlarmModePeriodFragment : Fragment() {
    private var dataBinding by autoCleared<FragmentAlarmModePeriodBinding>()
    private val viewModel: WaterAlarmViewModel by hiltNavGraphViewModels(R.id.alarm_nav_graph)
    private var period: AlarmModeSetting.Period = AlarmModeSetting.Period()

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
            val periodAlarm = it as? AlarmModeSetting.Period

            //해당 값으로 화면 구성
            periodAlarm?.let { period ->
                this@AlarmModePeriodFragment.period = period
                dataBinding.alarmWeek.setChecked(period.selectedDate)
                dataBinding.alarmWeek.setPeriodTime(
                    DateTimeUtils.getTime(
                        period.interval,
                        requireContext().getString(com.tkw.ui.R.string.hour),
                        requireContext().getString(com.tkw.ui.R.string.minute)
                    )
                )
                dataBinding.tvAlarmTime.text = it.getAlarmTimeRange()
                //todo 전부 클리어하고 설정된 시간에 interval대로 다시 전부 세팅
                setAlarm()
            }
        }
    }

    private fun initListener() {
        dataBinding.alarmWeek.setCheckListListener {
            updateModeSetting(
                period.copy(selectedDate = it)
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
                ).toNanoOfDay()
                updateModeSetting(
                    period.copy(interval = interval)
                )
            }
            dialog.show(childFragmentManager, dialog.tag)
        }
        dataBinding.clAlarmTimeEdit.setOnClickListener {
            showTimeDialog()
        }
    }

    private fun showTimeDialog() {
        val dialog = AlarmTimeBottomDialog(
            selectedStart = period.alarmStartTime,
            selectedEnd = period.alarmEndTime,
            resultListener = { start, end ->
                updateModeSetting(
                    period.copy(
                        alarmStartTime = start,
                        alarmEndTime = end!!
                    )
                )
            }
        )
        dialog.show(childFragmentManager, dialog.tag)
    }

    private fun updateModeSetting(period: AlarmModeSetting.Period?) {
        period?.let {
            viewModel.updateAlarmModeSetting(it)
        }
    }

    private fun setAlarm() {
        viewModel.clearAlarm(AlarmMode.PERIOD)
        if(dataBinding.alarmWeek.getCheckedList().isNotEmpty()) {
            //todo 테스트용 알람 세팅. 현재 선택된 요일과 알람 시간대에 맞춰서 인터벌대로 알람 호출 필요
            val alarm = Alarm(0, System.currentTimeMillis(), 1000 * 60, true)
            viewModel.setAlarm(alarm)
        }
    }
}