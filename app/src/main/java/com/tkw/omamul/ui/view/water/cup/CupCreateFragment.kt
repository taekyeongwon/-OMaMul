package com.tkw.omamul.ui.view.water.cup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tkw.omamul.R
import com.tkw.omamul.common.autoCleared
import com.tkw.omamul.common.getViewModelFactory
import com.tkw.omamul.data.model.Cup
import com.tkw.omamul.databinding.FragmentCupCreateBinding

class CupCreateFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentCupCreateBinding>()
    private val viewModel: CupViewModel by viewModels {
        val cupArgs: CupCreateFragmentArgs by navArgs()
        getViewModelFactory(cupArgs.cupArgument)
    }
    private var cupList: List<Cup> = arrayListOf()

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
        initObserver()
        initListener()
    }

    private fun initView() {
        dataBinding.viewModel = viewModel
    }

    private fun initObserver() {
        viewModel.cupListLiveData.observe(viewLifecycleOwner) {
            cupList = it
        }

        viewModel.createMode.observe(viewLifecycleOwner) {
            viewModel.buttonName.value =
                if(it) getString(R.string.add)
                else getString(R.string.modify)
        }

        viewModel.nextEvent.observe(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        viewModel.toastEvent.observe(viewLifecycleOwner) {
            Toast.makeText(
                requireContext(),
                it.getMessage(requireContext()),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            val isCreate = viewModel.createMode.value ?: false
            if(isCreate) viewModel.insertCup()
            else viewModel.updateCup()
        }
    }
}