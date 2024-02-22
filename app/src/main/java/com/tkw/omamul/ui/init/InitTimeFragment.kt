package com.tkw.omamul.ui.init

import androidx.fragment.app.viewModels
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.core.util.DateTimeUtils
import com.tkw.omamul.databinding.FragmentInitTimeBinding
import com.tkw.omamul.ui.base.BaseFragment
import com.tkw.omamul.ui.dialog.OnResultListener
import com.tkw.omamul.ui.dialog.AlarmTimeDialog

class InitTimeFragment: BaseFragment<FragmentInitTimeBinding, InitViewModel>(R.layout.fragment_init_time) {
    override val viewModel: InitViewModel by viewModels { ViewModelFactory }
    private lateinit var alarmTimeDialog: AlarmTimeDialog

    override fun initView() {
        setDefaultTime()
        initTimePicker(true)
    }

    override fun bindViewModel(binder: FragmentInitTimeBinding) {

    }

    override fun initObserver() {

    }

    override fun initListener() {
        dataBinding.tvWakeupTime.setOnClickListener {
            initTimePicker(true)
            showTimePicker()
        }
        dataBinding.tvSleepTime.setOnClickListener {
            initTimePicker(false)
            showTimePicker()
        }

        dataBinding.btnNext.setOnClickListener {
            nextFragment(R.id.initIntakeFragment)
        }
    }

    private fun setDefaultTime() {
        dataBinding.tvWakeupTime.text = DateTimeUtils.getFormattedTime(8, 0)
        dataBinding.tvSleepTime.text = DateTimeUtils.getFormattedTime(23, 0)
    }

    private fun initTimePicker(buttonFlag: Boolean) {
        alarmTimeDialog = AlarmTimeDialog(
            buttonFlag,
            DateTimeUtils.getTimeFromFormat(dataBinding.tvWakeupTime.text.toString())!!,
            DateTimeUtils.getTimeFromFormat(dataBinding.tvSleepTime.text.toString())!!
        )
        alarmTimeDialog.setResultListener(resultListener)
    }

    private fun showTimePicker() {
        alarmTimeDialog.show(childFragmentManager, alarmTimeDialog.tag)
    }

    private val resultListener = object : OnResultListener<String> {
        override fun onResult(vararg data: String) {
            dataBinding.tvWakeupTime.text = data[0]
            dataBinding.tvSleepTime.text = data[1]
        }
    }
}