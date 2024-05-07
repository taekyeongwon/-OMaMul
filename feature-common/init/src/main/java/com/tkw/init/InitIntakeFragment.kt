package com.tkw.init

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tkw.base.C
import com.tkw.common.autoCleared
import com.tkw.init.databinding.FragmentInitIntakeBinding
import com.tkw.navigation.DeepLinkDestination
import com.tkw.navigation.deepLinkNavigateTo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InitIntakeFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentInitIntakeBinding>()
    private val viewModel: InitViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentInitIntakeBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initListener()
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
                        val pref = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
                        pref.edit().putBoolean(C.FirstInstallFlag, true)?.apply()
                        findNavController().deepLinkNavigateTo(requireContext(), DeepLinkDestination.Home, true)
//                        findNavController().navigate(R.id.waterFragment)
//                        setStartDestination(R.id.waterFragment) //프래그먼트 이동 전에 호출하면 cannot be found from the current destination 에러 발생
                    }
                }
            }
        }

    }

    private fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            val amount = dataBinding.npAmount.getCurrentValue()
            viewModel.setEvent(InitContract.Event.SaveIntake(amount))
        }
    }

//    private fun setStartDestination(fragmentId: Int) {
//        val nav = findNavController()
//        val navGraph = nav.navInflater.inflate(R.navigation.nav_graph)
//        navGraph.setStartDestination(fragmentId)
//        nav.graph = navGraph
//    }
}