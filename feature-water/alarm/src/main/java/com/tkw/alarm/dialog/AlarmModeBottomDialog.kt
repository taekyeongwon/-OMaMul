package com.tkw.alarm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tkw.alarm.databinding.DialogAlarmModeBinding
import com.tkw.domain.model.AlarmMode
import com.tkw.ui.dialog.CustomBottomDialog

class AlarmModeBottomDialog(
    private var currentMode: AlarmMode,
    private val resultListener: (AlarmMode) -> Unit
): CustomBottomDialog<DialogAlarmModeBinding>() {
    override lateinit var childBinding: DialogAlarmModeBinding
    override var buttonCount: Int = 2

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

        initView()
        initListener()
    }

    private fun initView() {
        when(currentMode) {
            AlarmMode.PERIOD -> childBinding.clPeriod.isSelected = true
            AlarmMode.CUSTOM -> childBinding.clCustom.isSelected = true
        }
    }

    private fun initListener() {
        childBinding.clPeriod.setOnClickListener {
            currentMode = AlarmMode.PERIOD
            it.isSelected = true
            childBinding.clCustom.isSelected = false
        }
        childBinding.clCustom.setOnClickListener {
            currentMode = AlarmMode.CUSTOM
            it.isSelected = true
            childBinding.clPeriod.isSelected = false
        }

        setButtonListener(
            cancelAction = {
                dismiss()
            },
            confirmAction = {
                resultListener(currentMode)
                dismiss()
            }
        )
    }
}