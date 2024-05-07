package com.tkw.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tkw.ui.databinding.DialogTimepickerBinding
import com.tkw.ui.util.DateTimeUtils
import java.time.LocalTime

class AlarmTimeBottomDialog(
    private val buttonFlag: Boolean,
    private val selectedStart: LocalTime,
    private val selectedEnd: LocalTime,
    private val resultListener: (String, String) -> Unit
    ) : BottomSheetDialogFragment(), BottomExpand by BottomExpandImpl() {

    private lateinit var dataBinding: DialogTimepickerBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        onSetBottomBehavior(dialog)
        dataBinding = DialogTimepickerBinding.inflate(layoutInflater, container, false)
        return dataBinding.root
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

        dataBinding.rgSelector.setOnCheckedChangeListener(onCheckedChangeListener)
        setRadioChecked(buttonFlag)
    }

    private fun initListener() {
        dataBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        dataBinding.btnSave.setOnClickListener {
            sendSelectTime()
            dismiss()
        }
    }

    private fun initTimePicker(startHour: Int, startMin: Int, endHour: Int, endMin: Int) {
        dataBinding.apply {
            tpStart.hour = startHour
            tpStart.minute = startMin
            tpEnd.hour = endHour
            tpEnd.minute = endMin
        }
    }

    private fun setRadioChecked(flag: Boolean) {
        if(flag) dataBinding.rgSelector.check(R.id.rb_start)
        else dataBinding.rgSelector.check(R.id.rb_end)
    }

    private fun sendSelectTime() {
        val startTime =
            DateTimeUtils.getFormattedTime(
                dataBinding.tpStart.hour,
                dataBinding.tpStart.minute
            )
        val endTime =
            DateTimeUtils.getFormattedTime(
                dataBinding.tpEnd.hour,
                dataBinding.tpEnd.minute
            )
        resultListener(startTime, endTime)
    }

    private val onCheckedChangeListener =
        OnCheckedChangeListener { _, checkedId ->
            when(checkedId) {
                R.id.rb_start -> {
                    dataBinding.tpStart.visibility = View.VISIBLE
                    dataBinding.tpEnd.visibility = View.GONE
                }

                R.id.rb_end -> {
                    dataBinding.tpStart.visibility = View.GONE
                    dataBinding.tpEnd.visibility = View.VISIBLE
                }
            }
        }
}