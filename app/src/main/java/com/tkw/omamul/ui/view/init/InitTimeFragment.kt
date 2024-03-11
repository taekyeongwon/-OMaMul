package com.tkw.omamul.ui.view.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.common.util.DateTimeUtils
import com.tkw.omamul.databinding.FragmentInitTimeBinding
import com.tkw.omamul.ui.dialog.OnResultListener
import com.tkw.omamul.ui.dialog.AlarmTimeBottomDialog
import com.tkw.omamul.common.autoCleared

class InitTimeFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentInitTimeBinding>()
    private val viewModel: InitViewModel by viewModels { ViewModelFactory }
    private lateinit var alarmTimeDialog: AlarmTimeBottomDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentInitTimeBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        setDefaultTime()
        initTimePicker(true)
    }

    private fun initListener() {
        dataBinding.tvWakeupTime.setOnClickListener {
            initTimePicker(true)
            showTimePicker()
        }
        dataBinding.tvSleepTime.setOnClickListener {
            initTimePicker(false)
            showTimePicker()
        }

        dataBinding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.initIntakeFragment)
        }
    }

    private fun setDefaultTime() {
        dataBinding.tvWakeupTime.text = DateTimeUtils.getFormattedTime(8, 0)
        dataBinding.tvSleepTime.text = DateTimeUtils.getFormattedTime(23, 0)
    }

    private fun initTimePicker(buttonFlag: Boolean) {
        alarmTimeDialog = AlarmTimeBottomDialog(
            buttonFlag,
            DateTimeUtils.getTimeFromFormat(dataBinding.tvWakeupTime.text.toString()),
            DateTimeUtils.getTimeFromFormat(dataBinding.tvSleepTime.text.toString())
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