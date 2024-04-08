package com.tkw.omamul.ui.view.init

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tkw.omamul.R
import com.tkw.omamul.common.getViewModelFactory
import com.tkw.omamul.databinding.FragmentInitLanguageBinding
import com.tkw.omamul.common.autoCleared
import kotlinx.coroutines.launch

class InitLanguageFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentInitLanguageBinding>()
    private val viewModel: InitViewModel by viewModels { getViewModelFactory(null) }

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
        initObserver()
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

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect {

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sideEffect.collect {
                when(it) {
                    InitContract.SideEffect.OnMoveNext -> {
                        findNavController().navigate(R.id.initTimeFragment)
                    }
                }
            }
        }

    }

    private fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            val lang = when(dataBinding.rgLanguage.checkedRadioButtonId) {
                R.id.rb_ko -> "ko"
                R.id.rb_en -> "en"
                R.id.rb_jp -> "jp"
                R.id.rb_cn -> "cn"
                else -> ""
            }
            viewModel.setEvent(InitContract.Event.SaveLanguage(lang))
        }
    }

    private val callback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

        }
    }
}