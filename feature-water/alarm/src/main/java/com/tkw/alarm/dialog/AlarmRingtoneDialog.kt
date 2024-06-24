package com.tkw.alarm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tkw.alarm.databinding.DialogRingtoneBinding
import com.tkw.common.autoCleared
import com.tkw.domain.model.RingTone
import com.tkw.ui.dialog.CustomBottomDialog

class AlarmRingtoneDialog(
    private val resultListener: (RingTone) -> Unit
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
        //뷰모델에서 알람 세팅 가져와서 Ringtone 상태에 맞게. 디폴트는 현재 폰 설정
    }

    private fun initListener() {
        setButtonListener(
            cancelAction = {
                dismiss()
            },
            confirmAction = {
                sendRingtone()
                dismiss()
            }
        )
    }

    private fun sendRingtone() {
        //현재 선택된 벨소리 모드로 전달
        resultListener(RingTone.BELL)
    }
}