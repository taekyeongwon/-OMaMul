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
        initView()
        initListener()
    }

    private fun initView() {

    }

    private fun initListener() {
        setButtonListener(
            cancelAction = {
                dismiss()
            },
            confirmAction = {
                dismiss()
            }
        )
    }
}