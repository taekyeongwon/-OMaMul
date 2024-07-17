package com.tkw.alarm.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import com.tkw.alarm.R
import com.tkw.alarm.databinding.DialogTimepickerBinding
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.ui.dialog.CustomBottomDialog
import java.time.LocalTime

class AlarmTimeBottomDialog(
    private val buttonFlag: Boolean = true,
    private val selectedStart: LocalTime? = null,
    private val selectedEnd: LocalTime? = null,
    private val resultListener: (LocalTime, LocalTime?) -> Unit
    ) : CustomBottomDialog<DialogTimepickerBinding>() {
    override var childBinding by autoCleared<DialogTimepickerBinding>()
    override var buttonCount: Int = 2

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
        val startHour = selectedStart?.hour ?: 1
        val startMin = selectedStart?.minute ?: 0
        val endHour = selectedEnd?.hour ?: 23
        val endMin = selectedEnd?.minute ?: 0
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
            DateTimeUtils.getLocalTime(
                childBinding.tpStart.hour,
                childBinding.tpStart.minute
            )
        val endTime =
            DateTimeUtils.getLocalTime(
                childBinding.tpEnd.hour,
                childBinding.tpEnd.minute
            )

        if(childBinding.rgSelector.visibility == View.GONE) {
            resultListener(startTime, null)
        } else {
            resultListener(startTime, endTime)
        }
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

    fun setRadioButtonVisibility(isVisible: Boolean) {
        childBinding.rgSelector.visibility =
            if (isVisible) View.VISIBLE
            else View.GONE
    }
}