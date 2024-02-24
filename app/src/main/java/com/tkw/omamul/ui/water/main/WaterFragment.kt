package com.tkw.omamul.ui.water.main

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.data.model.CupEntity
import com.tkw.omamul.data.model.WaterEntity
import com.tkw.omamul.databinding.FragmentWaterBinding
import com.tkw.omamul.ui.adapter.OnAddListener
import com.tkw.omamul.ui.adapter.CupPagerAdapter
import com.tkw.omamul.ui.base.BaseFragment

class WaterFragment: BaseFragment<FragmentWaterBinding, WaterViewModel>(R.layout.fragment_water) {
    override val viewModel: WaterViewModel by activityViewModels { ViewModelFactory }
    private var countObject: List<WaterEntity>? = null
    private lateinit var cupPagerAdapter: CupPagerAdapter

    private var i = 0

    override fun initView() {
        initItemMenu()
        cupPagerAdapter = CupPagerAdapter(addListener)
        dataBinding.vpList.adapter = cupPagerAdapter
        dataBinding.vpList.offscreenPageLimit = 3
        val item = CupEntity()
        item.cupName = "test" + i++
        val currentList = cupPagerAdapter.currentList.toMutableList()
        currentList.add(item)
        cupPagerAdapter.submitList(currentList)
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

    private fun initItemMenu() {
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

    private val addListener = object: OnAddListener {
        override fun onClick() {
            val item = CupEntity()
            item.cupName = "test" + i++
            val currentList = cupPagerAdapter.currentList.toMutableList()
            currentList.add(item)
            cupPagerAdapter.submitList(currentList)
        }
    }
}