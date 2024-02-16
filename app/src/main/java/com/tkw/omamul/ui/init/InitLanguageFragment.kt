package com.tkw.omamul.ui.init

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.tkw.omamul.databinding.FragmentInitLanguageBinding

class InitLanguageFragment: Fragment() {
    private lateinit var dataBinding: FragmentInitLanguageBinding
    private val callback = object: OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {

        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = FragmentInitLanguageBinding.inflate(layoutInflater)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        dataBinding.btnFirst.setOnClickListener {
//            val startDestination = findNavController().graph.startDestinationId
//            val navOptions = NavOptions.Builder()
//                .setPopUpTo(startDestination, true)
//                .build()
//            findNavController().navigate(startDestination, null, navOptions)
//        }
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}