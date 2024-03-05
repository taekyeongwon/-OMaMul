package com.tkw.omamul.ui.view.init

import android.widget.Toast
import androidx.fragment.app.viewModels
import com.tkw.omamul.MainApplication
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.util.C
import com.tkw.omamul.databinding.FragmentInitIntakeBinding
import com.tkw.omamul.ui.base.BaseFragment

class InitIntakeFragment: BaseFragment<FragmentInitIntakeBinding, InitViewModel>(R.layout.fragment_init_intake) {
    override val viewModel: InitViewModel by viewModels { ViewModelFactory }

    override fun initView() {

    }

    override fun bindViewModel(binder: FragmentInitIntakeBinding) {

    }

    override fun initObserver() {

    }

    override fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            MainApplication.sharedPref?.edit()?.putBoolean(C.FirstInstallFlag, true)?.apply()
            nextFragment(R.id.waterFragment)
            setStartDestination(R.id.waterFragment) //프래그먼트 이동 전에 호출하면 cannot be found from the current destination 에러 발생
        }
    }
}