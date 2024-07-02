package com.tkw.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tkw.alarm.databinding.FragmentDetailPeriodBinding
import com.tkw.common.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailAlarmPeriodFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentDetailPeriodBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentDetailPeriodBinding.inflate(inflater, container, false)
        return dataBinding.root
    }
}