package com.tkw.alarm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.tkw.alarm.databinding.FragmentWaterAlarmDetailBinding
import com.tkw.common.autoCleared
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailAlarmFragment: Fragment() {
    private var dataBinding by autoCleared<FragmentWaterAlarmDetailBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = FragmentWaterAlarmDetailBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        replaceFragment(DetailAlarmPeriodFragment())

        dataBinding.tvAlarmMode.setOnClickListener {
            replaceFragment(DetailAlarmPeriodFragment())
        }

        dataBinding.tvAlarmMode2.setOnClickListener {
            replaceFragment(DetailAlarmCustomFragment())
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
    }
}