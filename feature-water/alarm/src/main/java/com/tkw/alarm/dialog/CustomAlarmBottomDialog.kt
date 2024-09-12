package com.tkw.alarm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isNotEmpty
import com.tkw.alarm.databinding.DialogCustomAlarmBinding
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.common.util.toEpochMilli
import com.tkw.domain.model.Alarm
import com.tkw.ui.dialog.CustomBottomDialog
import java.time.DayOfWeek

class CustomAlarmBottomDialog(
    private val alarm: Alarm,
    private val resultListener: (Alarm) -> Unit
): CustomBottomDialog<DialogCustomAlarmBinding>()  {
    override var childBinding: DialogCustomAlarmBinding by autoCleared()
    override var buttonCount: Int = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        childBinding = DialogCustomAlarmBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        val startHour = DateTimeUtils.Time.getLocalTime(alarm.startTime).hour
        val startMin = DateTimeUtils.Time.getLocalTime(alarm.startTime).minute
        initTimePicker(startHour, startMin)
        initWeekList(alarm.weekList)
    }

    private fun initTimePicker(startHour: Int, startMin: Int) {
        childBinding.apply {
            tpStart.hour = startHour
            tpStart.minute = startMin
        }
    }

    private fun initWeekList(list: List<DayOfWeek>) {
        childBinding.alarmWeek.setChecked(list)
    }

    private fun initListener() {
        setButtonListener(
            cancelAction = {
                dismiss()
            },
            confirmAction = {
                sendSelectTime()
                dismiss()
            }
        )
    }

    private fun sendSelectTime() {
        val startTime =
            DateTimeUtils.Time.getLocalTime(
                childBinding.tpStart.hour,
                childBinding.tpStart.minute
            ).toEpochMilli()
        resultListener(
            alarm.copy(
                startTime = startTime,
                weekList = childBinding.alarmWeek.getCheckedList(),
                enabled = childBinding.alarmWeek.isNotEmpty()
            )
        )
    }
}