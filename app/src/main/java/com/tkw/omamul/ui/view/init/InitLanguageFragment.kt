package com.tkw.omamul.ui.view.init

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.FragmentInitLanguageBinding
import com.tkw.omamul.util.autoCleared

class InitLanguageFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentInitLanguageBinding>()
    private val viewModel: InitViewModel by viewModels { ViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentInitLanguageBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    private fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.initTimeFragment)
        }
    }

    private val callback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

        }
    }
}