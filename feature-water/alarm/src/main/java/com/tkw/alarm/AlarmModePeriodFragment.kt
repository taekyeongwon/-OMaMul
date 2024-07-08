package com.tkw.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tkw.alarm.databinding.FragmentAlarmModePeriodBinding
import com.tkw.common.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmModePeriodFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentAlarmModePeriodBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentAlarmModePeriodBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initObserver()
        initListener()
    }

    private fun initView() {

    }

    private fun initObserver() {

    }

    private fun initListener() {
        dataBinding.alarmWeek.setCheckListListener {  }
        dataBinding.alarmWeek.setPeriodClickListener {  }
        dataBinding.clAlarmTimeEdit.setOnClickListener {  }
    }
}