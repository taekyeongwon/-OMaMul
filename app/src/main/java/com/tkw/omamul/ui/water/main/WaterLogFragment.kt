package com.tkw.omamul.ui.water.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tkw.omamul.R
import com.tkw.omamul.databinding.FragmentWaterLogBinding

class WaterLogFragment: Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentWaterLogBinding.inflate(layoutInflater).root
    }
}