package com.tkw.omamul.ui.water.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.tkw.omamul.MainApplication
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.data.model.WaterEntity
import com.tkw.omamul.databinding.FragmentWaterBinding
import com.tkw.omamul.ui.base.BaseFragment

class WaterFragment: BaseFragment<FragmentWaterBinding, WaterViewModel>(R.layout.fragment_water) {
    override val viewModel: WaterViewModel by activityViewModels { ViewModelFactory }
    private var countObject: List<WaterEntity>? = null

    override fun initView() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.toolbar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return menuItem.onNavDestinationSelected(findNavController())
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun bindViewModel(binder: FragmentWaterBinding) {
        with(binder) {
            viewModel = this@WaterFragment.viewModel
        }
    }

    override fun initObserver() {
        viewModel.countStreamLiveData.observe(this, Observer {
            countObject = it.dayOfList
//            dataBinding.tvCount.text = "${
//                it.dayOfList.sumOf { water ->
//                    water.amount
//                }
//            }"
        })
    }

    override fun initListener() {
        dataBinding.btnAdd.setOnClickListener {
            viewModel.addCount()
        }

        dataBinding.btnRemove.setOnClickListener {
            if(!countObject.isNullOrEmpty()) {
                viewModel.removeCount(countObject!!.last())
            }
        }
    }


}