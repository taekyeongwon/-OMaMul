package com.tkw.omamul.ui.init

import androidx.fragment.app.viewModels
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.FragmentInitTimeBinding
import com.tkw.omamul.ui.base.BaseFragment

class InitTimeFragment: BaseFragment<FragmentInitTimeBinding, InitViewModel>(R.layout.fragment_init_time) {
    override val viewModel: InitViewModel by viewModels { ViewModelFactory }

    override fun initView() {

    }

    override fun bindViewModel(binder: FragmentInitTimeBinding) {

    }

    override fun initObserver() {

    }

    override fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            nextFragment(R.id.initIntakeFragment)
        }
    }
}