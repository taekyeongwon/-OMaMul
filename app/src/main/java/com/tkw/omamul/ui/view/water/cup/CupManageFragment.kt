package com.tkw.omamul.ui.view.water.cup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.common.autoCleared
import com.tkw.omamul.databinding.FragmentCupManageBinding
import com.tkw.omamul.ui.view.water.cup.adapter.CupListAdapter
import com.tkw.omamul.ui.view.water.main.WaterViewModel

class CupManageFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentCupManageBinding>()
    private val viewModel: WaterViewModel by viewModels { ViewModelFactory }
    private lateinit var cupListAdapter: CupListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentCupManageBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        cupListAdapter = CupListAdapter(editListener, deleteListener, addListener)
        dataBinding.rvCupList.adapter = cupListAdapter
    }

    private fun initListener() {

    }

    private val editListener: (Int) -> Unit = { position ->

    }

    private val deleteListener: (Int) -> Unit = { position ->

    }

    private val addListener: () -> Unit = {
        findNavController().navigate(R.id.cupCreateFragment)
    }
}