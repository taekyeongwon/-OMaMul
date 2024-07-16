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
import com.tkw.domain.model.AlarmModeSetting
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalTime

@AndroidEntryPoint
class AlarmModePeriodFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentAlarmModePeriodBinding>()
    private val viewModel: WaterAlarmViewModel by hiltNavGraphViewModels(R.id.alarm_nav_graph)
    private var period: AlarmModeSetting.Period? = null

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

    }

    private fun initObserver() {
        viewModel.periodModeSettingsLiveData.observe(viewLifecycleOwner) {
            val periodAlarm = it as? AlarmModeSetting.Period
            period = periodAlarm
            //해당 값으로 화면 구성
            periodAlarm?.let { period ->
                dataBinding.alarmWeek.setChecked(period.selectedDate)
                dataBinding.alarmWeek.setPeriodTime(DateTimeUtils.getTime(
                    period.interval,
                    requireContext().getString(com.tkw.ui.R.string.hour),
                    requireContext().getString(com.tkw.ui.R.string.minute)
                ))
                dataBinding.tvAlarmTime.text = it.getAlarmTimeRange()
            }
        }
    }

    private fun initListener() {
        dataBinding.alarmWeek.setCheckListListener {
            it.forEach {
                Log.d("week", it.toString())
            }
        }
        dataBinding.alarmWeek.setPeriodClickListener {
            val dialog = AlarmPeriodDialog(
                DateTimeUtils.getTimeFromLocalTime(
                    dataBinding.alarmWeek.getPeriodTime(),
                    requireContext().getString(com.tkw.ui.R.string.hour),
                    requireContext().getString(com.tkw.ui.R.string.minute)
                )
            ) {
                dataBinding.alarmWeek.setPeriodTime(it)
            }
            dialog.show(childFragmentManager, dialog.tag)
        }
        dataBinding.clAlarmTimeEdit.setOnClickListener {
            showTimeDialog()
        }
    }

    private fun showTimeDialog() {
        val dialog = AlarmTimeBottomDialog(
            selectedStart = period?.alarmStartTime ?: LocalTime.now(),
            selectedEnd = period?.alarmEndTime ?: LocalTime.now(),
            resultListener = { start, end ->
                //todo 전부 클리어하고 설정된 시간에 interval대로 다시 전부 세팅
            }
        )
        dialog.show(childFragmentManager, dialog.tag)
    }
}