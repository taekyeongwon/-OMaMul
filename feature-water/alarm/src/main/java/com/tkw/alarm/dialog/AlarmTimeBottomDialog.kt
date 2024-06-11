package com.tkw.alarm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import com.tkw.alarm.R
import com.tkw.alarm.databinding.DialogTimepickerBinding
import com.tkw.common.autoCleared
import com.tkw.ui.dialog.CustomBottomDialog
import com.tkw.common.util.DateTimeUtils
import java.time.LocalTime

class AlarmTimeBottomDialog(
    private val buttonFlag: Boolean,
    private val selectedStart: LocalTime,
    private val selectedEnd: LocalTime,
    private val resultListener: (String, String) -> Unit
    ) : CustomBottomDialog<DialogTimepickerBinding>() {
    override var childBinding by autoCleared<DialogTimepickerBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        childBinding = DialogTimepickerBinding.inflate(layoutInflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        val startHour = selectedStart.hour
        val startMin = selectedStart.minute
        val endHour = selectedEnd.hour
        val endMin = selectedEnd.minute
        initTimePicker(startHour, startMin, endHour, endMin)

        childBinding.rgSelector.setOnCheckedChangeListener(onCheckedChangeListener)
        setRadioChecked(buttonFlag)
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

    private fun initTimePicker(startHour: Int, startMin: Int, endHour: Int, endMin: Int) {
        childBinding.apply {
            tpStart.hour = startHour
            tpStart.minute = startMin
            tpEnd.hour = endHour
            tpEnd.minute = endMin
        }
    }

    private fun setRadioChecked(flag: Boolean) {
        if(flag) childBinding.rgSelector.check(R.id.rb_start)
        else childBinding.rgSelector.check(R.id.rb_end)
    }

    private fun sendSelectTime() {
        val startTime =
            DateTimeUtils.getFormattedTime(
                childBinding.tpStart.hour,
                childBinding.tpStart.minute
            )
        val endTime =
            DateTimeUtils.getFormattedTime(
                childBinding.tpEnd.hour,
                childBinding.tpEnd.minute
            )
        resultListener(startTime, endTime)
    }

    private val onCheckedChangeListener =
        OnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.rb_start -> {
                    childBinding.tpStart.visibility = View.VISIBLE
                    childBinding.tpEnd.visibility = View.GONE
                }

                R.id.rb_end -> {
                    childBinding.tpStart.visibility = View.GONE
                    childBinding.tpEnd.visibility = View.VISIBLE
                }
            }
        }
}