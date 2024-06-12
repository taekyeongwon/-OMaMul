package com.tkw.alarm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tkw.alarm.databinding.DialogRingtoneBinding
import com.tkw.common.autoCleared
import com.tkw.ui.dialog.CustomBottomDialog

class AlarmRingtoneDialog(
    private val resultListener: (String) -> Unit
): CustomBottomDialog<DialogRingtoneBinding>() {
    override var childBinding by autoCleared<DialogRingtoneBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        childBinding = DialogRingtoneBinding.inflate(layoutInflater, container, false)
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