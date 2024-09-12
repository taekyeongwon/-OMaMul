package com.tkw.init

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.tkw.alarm.dialog.ExactAlarmDialog
import com.tkw.common.autoCleared
import com.tkw.domain.IAlarmManager
import com.tkw.init.databinding.FragmentInitIntakeBinding
import com.tkw.navigation.DeepLinkDestination
import com.tkw.navigation.deepLinkNavigateTo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InitIntakeFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentInitIntakeBinding>()
    private val viewModel: InitViewModel by viewModels()

    @Inject
    lateinit var alarmManager: IAlarmManager

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
                if(it is InitContract.State.Complete) {
                    checkApi31ExactAlarm()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sideEffect.collect {
                if(it is InitContract.SideEffect.OnMoveNext) {
                    findNavController().deepLinkNavigateTo(requireContext(), DeepLinkDestination.Home, true)
                }
            }
        }

    }

    private fun checkApi31ExactAlarm() {
        if(Build.VERSION.SDK_INT >= 31 &&
            !alarmManager.canScheduleExactAlarms() ) {
            val dialog = ExactAlarmDialog(
                true,
                cancelAction = {
                    viewModel.setEvent(InitContract.Event.SaveInitialFlag(true))
                },
                confirmAction = {
                    viewModel.setEvent(InitContract.Event.SaveInitialFlag(true))
                }
            )
            dialog.show(childFragmentManager, dialog.tag)
            dialog.isCancelable = false
        } else {
            viewModel.setEvent(InitContract.Event.SaveInitialFlag(true))
        }
    }

    private fun initListener() {
        dataBinding.btnNext.setOnClickListener {
            val amount = dataBinding.npAmount.getCurrentValue()
            viewModel.setEvent(InitContract.Event.SaveIntake(amount))
        }
    }
}