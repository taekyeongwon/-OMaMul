package com.tkw.alarm.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withResumed
import com.tkw.alarm.R
import com.tkw.alarm.WaterAlarmViewModel
import com.tkw.alarm.databinding.DialogRingtoneBinding
import com.tkw.common.autoCleared
import com.tkw.domain.model.RingTone
import com.tkw.domain.model.RingToneMode
import com.tkw.ui.dialog.CustomBottomDialog
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AlarmRingtoneDialog(
    private val resultListener: (RingTone) -> Unit
): CustomBottomDialog<DialogRingtoneBinding>() {
    override var childBinding by autoCleared<DialogRingtoneBinding>()
    private val viewModel: WaterAlarmViewModel by hiltNavGraphViewModels(R.id.alarm_nav_graph)
    override var buttonCount: Int = 2

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

        initObserver()
        initListener()
    }

    private fun initObserver() {
        viewModel.alarmRingTone.observe(viewLifecycleOwner) {
            when(it.getCurrentMode()) {
                RingTone.BELL -> setRingtoneChecked(it.isBell)
                RingTone.VIBE -> setVibeChecked(it.isVibe)
                RingTone.ALL -> setAllChecked()
                RingTone.IGNORE -> setIgnore()
                RingTone.DEVICE -> setDeviceChecked(it.isDevice)
            }
            setNotiChecked(it.isSilence)
        }
    }

    private fun initListener() {
        setButtonListener(
            cancelAction = {
                dismiss()
            },
            confirmAction = {
                update()
                dismiss()
            }
        )

        with(childBinding) {
            svRingtone.setCheckedChangeListener { compoundButton, b ->
                if(b) {
                    svPhoneSetting.setChecked(false)
                }
            }
            svVibe.setCheckedChangeListener { compoundButton, b ->
                if(b) {
                    svPhoneSetting.setChecked(false)
                }
            }
            svPhoneSetting.setCheckedChangeListener { compoundButton, b ->
                if(b) {
                    svRingtone.setChecked(false)
                    svVibe.setChecked(false)
                }
            }
        }
    }

    private fun setRingtoneChecked(flag: Boolean) {
        childBinding.svRingtone.setChecked(flag)
    }

    private fun setVibeChecked(flag: Boolean) {
        childBinding.svVibe.setChecked(flag)
    }

    private fun setAllChecked() {
        childBinding.svRingtone.setChecked(true)
        childBinding.svVibe.setChecked(true)
    }

    private fun setIgnore() {
        childBinding.svRingtone.setChecked(false)
        childBinding.svVibe.setChecked(false)
        childBinding.svPhoneSetting.setChecked(false)
    }

    private fun setDeviceChecked(flag: Boolean) {
        childBinding.svPhoneSetting.setChecked(flag)
    }

    private fun setNotiChecked(flag: Boolean) {
        childBinding.svNoti.setChecked(flag)
    }

    private fun update() {
        viewModel.updateRingToneMode(
            RingToneMode(
                isBell = childBinding.svRingtone.getChecked(),
                isVibe = childBinding.svVibe.getChecked(),
                isDevice = childBinding.svPhoneSetting.getChecked(),
                isSilence = childBinding.svNoti.getChecked()
            )
        )
    }
}