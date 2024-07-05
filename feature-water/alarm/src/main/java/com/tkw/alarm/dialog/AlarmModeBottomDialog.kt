package com.tkw.alarm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tkw.alarm.databinding.DialogAlarmModeBinding
import com.tkw.domain.model.AlarmMode

class AlarmModeBottomDialog(
    private val resultListener: (AlarmMode) -> Unit
): BottomSheetDialogFragment() {
    private lateinit var childBinding: DialogAlarmModeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        childBinding = DialogAlarmModeBinding.inflate(inflater, container, false)
        return childBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListener()
    }

    private fun initListener() {
        childBinding.clPeriod.setOnClickListener {
            resultListener(AlarmMode.Period())  //todo 현재 저장된 세팅에서 가져오기.
            dismiss()
        }
        childBinding.clCustom.setOnClickListener {
            resultListener(AlarmMode.Custom())  //todo 현재 저장된 세팅에서 가져오기.
            dismiss()
        }
    }
}