package com.tkw.omamul.ui.water.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.tkw.omamul.MainApplication
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.data.model.WaterEntity
import com.tkw.omamul.databinding.ActivityWaterBinding
import com.tkw.omamul.ui.base.BaseActivity

class WaterActivity : BaseActivity<ActivityWaterBinding, WaterViewModel>(R.layout.activity_water) {

    override val isSplash: Boolean = true
    override val viewModel:WaterViewModel by viewModels { ViewModelFactory }

    override fun initView() {
        setSupportActionBar(dataBinding.toolbar)
        val nav = findNavController(R.id.fragment_container_view)

        nav.addOnDestinationChangedListener { _, destination, _ ->
            //여기서 2depth부턴 바텀네비 숨기기
        }

        if(MainApplication.sharedPref?.getBoolean("test", false) == false) {
            MainApplication.sharedPref?.edit()?.putBoolean("test", true)?.apply()
//            findNavController().navigate(R.id.FirstStartFragment, null, navOptions)
//            nextFragment(R.id.FirstStartFragment)
            val navGraph = nav.graph
            navGraph.setStartDestination(R.id.FirstStartFragment)
            nav.graph = navGraph
        }

        NavigationUI.setupWithNavController(dataBinding.bottomNav, nav)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.WaterFragment, R.id.waterLogFragment, R.id.settingFragment))
        NavigationUI.setupWithNavController(dataBinding.toolbar, nav, appBarConfiguration)
//        viewModel.getCount()
    }

    override fun bindViewModel(binder: ActivityWaterBinding) {
        with(binder) {
            viewModel = this@WaterActivity.viewModel
        }
    }

    override fun initObserver() {

    }

    override fun initListener() {

    }

    private fun initNavigate() {

    }
}