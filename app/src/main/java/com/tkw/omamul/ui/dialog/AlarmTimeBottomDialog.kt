package com.tkw.omamul.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup.OnCheckedChangeListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tkw.omamul.R
import com.tkw.omamul.util.DateTimeUtils
import com.tkw.omamul.databinding.DialogTimepickerBinding
import java.util.Calendar

class AlarmTimeBottomDialog(
    private val buttonFlag: Boolean,
    private val selectedStart: Calendar,
    private val selectedEnd: Calendar
    ) : BottomSheetDialogFragment(), BottomExpandDelegation by BottomExpandDelegationImpl() {

    private lateinit var dataBinding: DialogTimepickerBinding
    private var resultListener: OnResultListener<String>? = null

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
        val startHour = selectedStart.get(Calendar.HOUR_OF_DAY)
        val startMin = selectedStart.get(Calendar.MINUTE)
        val endHour = selectedEnd.get(Calendar.HOUR_OF_DAY)
        val endMin = selectedEnd.get(Calendar.MINUTE)
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

    fun setResultListener(listener: OnResultListener<String>) {
        resultListener = listener
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

    private fun validateCheck(): Boolean {  //새벽에 기상하는 경우 취침시간이 더 빠를 수 있으므로 사용 x
        val startCalendar = Calendar.getInstance()
        startCalendar.clear()
        startCalendar.set(Calendar.HOUR_OF_DAY, dataBinding.tpStart.hour)
        startCalendar.set(Calendar.MINUTE, dataBinding.tpStart.minute)

        val endCalendar = Calendar.getInstance()
        endCalendar.clear()
        endCalendar.set(Calendar.HOUR_OF_DAY, dataBinding.tpEnd.hour)
        endCalendar.set(Calendar.MINUTE, dataBinding.tpEnd.minute)

        return startCalendar.after(endCalendar)
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
        resultListener?.onResult(startTime, endTime)
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