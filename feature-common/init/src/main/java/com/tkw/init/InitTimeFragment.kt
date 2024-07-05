package com.tkw.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tkw.alarm.dialog.AlarmTimeBottomDialog
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.init.databinding.FragmentInitTimeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InitTimeFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentInitTimeBinding>()
    private val viewModel: InitViewModel by viewModels()
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
        initObserver()
        initListener()
    }

    private fun initView() {
        setDefaultTime()
        initTimePicker(true)
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sideEffect.collect {
                when(it) {
                    InitContract.SideEffect.OnMoveNext -> {
                        findNavController().navigate(R.id.initIntakeFragment)
                    }
                    is InitContract.SideEffect.InitTimePicker -> {
                        initTimePicker(it.flag)
                        showTimePicker()
                    }
                }
            }
        }

    }

    private fun initListener() {
        dataBinding.tvWakeupTime.setOnClickListener {
            viewModel.setEvent(InitContract.Event.ClickWakeUpTimePicker)
        }
        dataBinding.tvSleepTime.setOnClickListener {
            viewModel.setEvent(InitContract.Event.ClickSleepTimePicker)
        }

        dataBinding.btnNext.setOnClickListener {
            val wakeTime = dataBinding.tvWakeupTime.text.toString()
            val sleepTime = dataBinding.tvSleepTime.text.toString()
            viewModel.setEvent(InitContract.Event.SaveTime(wakeTime, sleepTime))
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
            DateTimeUtils.getTimeFromFormat(dataBinding.tvSleepTime.text.toString()),
            resultListener
        )
    }

    private fun showTimePicker() {
        alarmTimeDialog.show(childFragmentManager, alarmTimeDialog.tag)
    }

    private val resultListener: (String, String?) -> Unit = { wake, sleep ->
            dataBinding.tvWakeupTime.text = wake
            dataBinding.tvSleepTime.text = sleep
    }
}