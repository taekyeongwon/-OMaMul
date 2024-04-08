package com.tkw.omamul.ui.view.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tkw.omamul.R
import com.tkw.omamul.common.getViewModelFactory
import com.tkw.omamul.common.util.DateTimeUtils
import com.tkw.omamul.databinding.FragmentInitTimeBinding
import com.tkw.omamul.ui.dialog.OnResultListener
import com.tkw.omamul.ui.dialog.AlarmTimeBottomDialog
import com.tkw.omamul.common.autoCleared
import kotlinx.coroutines.launch

class InitTimeFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentInitTimeBinding>()
    private val viewModel: InitViewModel by viewModels { getViewModelFactory(null) }
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
            viewModel.state.collect {
                if(it is InitContract.State.InitTimePicker) {
                    initTimePicker(it.flag)
                    showTimePicker()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sideEffect.collect {
                when(it) {
                    InitContract.SideEffect.OnMoveNext -> {
                        findNavController().navigate(R.id.initIntakeFragment)
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