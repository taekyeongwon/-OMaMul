package com.tkw.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.tkw.alarm.databinding.FragmentAlarmModeBinding
import com.tkw.alarm.dialog.AlarmModeBottomDialog
import com.tkw.common.autoCleared
import com.tkw.domain.model.AlarmMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmModeFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentAlarmModeBinding>()
    private val fragmentList by lazy {
        listOf(
            AlarmModePeriodFragment(),
            AlarmModeCustomFragment()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentAlarmModeBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initListener()
    }

    private fun initView() {
        replaceFragment(fragmentList[0])    //todo navArgs로 전달받은 모드의 프래그먼트로 설정
    }

    private fun initListener() {
        dataBinding.tvAlarmMode.setOnClickListener {
            val dialog = AlarmModeBottomDialog {
                if(it is AlarmMode.Period) {
                    replaceFragment(fragmentList[0])
                } else {
                    replaceFragment(fragmentList[1])
                }
            }
            dialog.show(childFragmentManager, dialog.tag)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.commit {
            val currentFragment =
                childFragmentManager.fragments.firstOrNull { fr -> fr.isVisible }
            currentFragment?.let { hide(it) }
            if(fragment.isAdded) {
                show(fragment)
            } else {
                add(dataBinding.container.id, fragment, fragment.tag)
                    .show(fragment)
            }
        }
        setAlarmModeText(fragment)
    }

    private fun setAlarmModeText(fragment: Fragment) {
        val text = when(fragment) {
            is AlarmModePeriodFragment -> getString(com.tkw.ui.R.string.alarm_mode_period)
            is AlarmModeCustomFragment -> getString(com.tkw.ui.R.string.alarm_mode_custom)
            else -> ""
        }
        dataBinding.tvAlarmMode.setText(text)
    }
}