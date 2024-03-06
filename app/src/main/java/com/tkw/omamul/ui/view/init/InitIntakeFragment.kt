package com.tkw.omamul.ui.view.init

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tkw.omamul.MainApplication
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.util.C
import com.tkw.omamul.databinding.FragmentInitIntakeBinding
import com.tkw.omamul.util.autoCleared

class InitIntakeFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentInitIntakeBinding>()
    private val viewModel: InitViewModel by viewModels { ViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dataBinding = FragmentInitIntakeBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            MainApplication.sharedPref?.edit()?.putBoolean(C.FirstInstallFlag, true)?.apply()
            findNavController().navigate(R.id.waterFragment)
            setStartDestination(R.id.waterFragment) //프래그먼트 이동 전에 호출하면 cannot be found from the current destination 에러 발생
        }
    }

    private fun setStartDestination(fragmentId: Int) {
        val nav = findNavController()
        val navGraph = nav.navInflater.inflate(R.navigation.nav_graph)
        navGraph.setStartDestination(fragmentId)
        nav.graph = navGraph
    }
}