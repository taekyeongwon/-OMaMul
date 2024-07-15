package com.tkw.alarm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tkw.alarm.databinding.DialogAlarmModeBinding
import com.tkw.domain.model.AlarmMode
import com.tkw.ui.dialog.CustomBottomDialog

class AlarmModeBottomDialog(
    private val resultListener: (AlarmMode) -> Unit
): CustomBottomDialog<DialogAlarmModeBinding>() {
    override lateinit var childBinding: DialogAlarmModeBinding
    override var buttonCount: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        childBinding = DialogAlarmModeBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    private fun initListener() {
        childBinding.clPeriod.setOnClickListener {
            resultListener(AlarmMode.PERIOD)
            dismiss()
        }
        childBinding.clCustom.setOnClickListener {
            resultListener(AlarmMode.CUSTOM)
            dismiss()
        }
    }
}