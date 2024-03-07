package com.tkw.omamul.ui.view.water.cup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewbinding.ViewBinding
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.common.autoCleared
import com.tkw.omamul.databinding.FragmentCupCreateBinding
import com.tkw.omamul.databinding.FragmentWaterIntakeBinding
import com.tkw.omamul.ui.view.water.main.WaterViewModel

class CupCreateFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentCupCreateBinding>()
    private val viewModel: WaterViewModel by viewModels { ViewModelFactory }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentCupCreateBinding.inflate(layoutInflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {

    }
}