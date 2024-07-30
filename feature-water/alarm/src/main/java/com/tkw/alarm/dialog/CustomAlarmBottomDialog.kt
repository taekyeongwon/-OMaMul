package com.tkw.alarm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tkw.alarm.databinding.DialogCustomAlarmBinding
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.ui.dialog.CustomBottomDialog
import java.time.LocalTime

class CustomAlarmBottomDialog(
    private val selectedStart: LocalTime? = null,
    private val resultListener: (LocalTime) -> Unit
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
        val startHour = selectedStart?.hour ?: 1
        val startMin = selectedStart?.minute ?: 0
        initTimePicker(startHour, startMin)
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

    private fun initTimePicker(startHour: Int, startMin: Int) {
        childBinding.apply {
            tpStart.hour = startHour
            tpStart.minute = startMin
        }
    }

    private fun sendSelectTime() {
        val startTime =
            DateTimeUtils.getLocalTime(
                childBinding.tpStart.hour,
                childBinding.tpStart.minute
            )
        resultListener(startTime)
    }
}