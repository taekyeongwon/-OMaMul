package com.tkw.omamul.ui.view.init

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.FragmentInitLanguageBinding
import com.tkw.omamul.ui.base.BaseFragment

class InitLanguageFragment: BaseFragment<FragmentInitLanguageBinding, InitViewModel>(R.layout.fragment_init_language) {
    override val viewModel: InitViewModel by viewModels { ViewModelFactory }

    override fun initView() {

    }

    override fun bindViewModel(binder: FragmentInitLanguageBinding) {

    }

    override fun initObserver() {

    }

    override fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            nextFragment(R.id.initTimeFragment)
        }
        //        dataBinding.btnFirst.setOnClickListener { -> 마지막 initIntake에서 start 바꿔주기
//            val startDestination = findNavController().graph.startDestinationId
//            val navOptions = NavOptions.Builder()
//                .setPopUpTo(startDestination, true)
//                .build()
//            findNavController().navigate(startDestination, null, navOptions)
//        }
    }

    private val callback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}