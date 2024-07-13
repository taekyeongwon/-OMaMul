package com.tkw.alarm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tkw.alarm.databinding.DialogAlarmPeriodBinding
import com.tkw.common.autoCleared
import com.tkw.ui.dialog.CustomBottomDialog

class AlarmPeriodDialog(
    private val resultListener: (String) -> Unit
): CustomBottomDialog<DialogAlarmPeriodBinding>() {
    override var childBinding by autoCleared<DialogAlarmPeriodBinding>()
    override var buttonCount: Int = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        childBinding = DialogAlarmPeriodBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        setButtonListener(
            cancelAction = {
                dismiss()
            },
            confirmAction = {
                sendPeriod()
                dismiss()
            }
        )
    }

    private fun sendPeriod() {
        resultListener(childBinding.timePicker.getValue())
    }
}