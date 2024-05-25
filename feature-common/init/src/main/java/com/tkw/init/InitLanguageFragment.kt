package com.tkw.init

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tkw.common.LocaleHelper
import com.tkw.common.autoCleared
import com.tkw.init.databinding.FragmentInitLanguageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InitLanguageFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentInitLanguageBinding>()
    private val viewModel: InitViewModel by activityViewModels()

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
//        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
//        callback.remove()
    }

    private fun initObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sideEffect.collect {
                if(it is InitContract.SideEffect.OnMoveNext) {
                    findNavController().navigate(R.id.initTimeFragment)
                }
            }
        }

    }

    private fun initListener() {
        dataBinding.rgLanguage.setOnCheckedChangeListener { group, checkedId ->
            val lang = when(checkedId) {
                R.id.rb_ko -> "ko"
                R.id.rb_en -> "en"
                R.id.rb_jp -> "ja"
                R.id.rb_cn -> "zh"
                else -> "ko"
            }
            LocaleHelper.setApplicationLocales(requireActivity(), lang)
        }

        dataBinding.btnNext.setOnClickListener {
            if(dataBinding.rgLanguage.checkedRadioButtonId != -1) {
                val lang = when (dataBinding.rgLanguage.checkedRadioButtonId) {
                    R.id.rb_ko -> "ko"
                    R.id.rb_en -> "en"
                    R.id.rb_jp -> "ja"
                    R.id.rb_cn -> "zh"
                    else -> "ko"
                }
                viewModel.setEvent(InitContract.Event.SaveLanguage(lang))
            }
        }
    }

    private val callback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

        }
    }
}