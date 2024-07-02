package com.tkw.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tkw.alarm.databinding.FragmentDetailCustomBinding
import com.tkw.common.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailAlarmCustomFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentDetailCustomBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentDetailCustomBinding.inflate(inflater, container, false)
        return dataBinding.root
    }
}