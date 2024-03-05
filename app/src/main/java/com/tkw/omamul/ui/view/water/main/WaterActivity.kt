package com.tkw.omamul.ui.view.water.main

import android.view.View
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.tkw.omamul.MainApplication
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.util.C
import com.tkw.omamul.databinding.ActivityWaterBinding
import com.tkw.omamul.ui.base.BaseActivity

class WaterActivity : BaseActivity<ActivityWaterBinding, WaterViewModel>(R.layout.activity_water) {

    override val isSplash: Boolean = true
    override val viewModel: WaterViewModel by viewModels { ViewModelFactory }
    private val mainFragmentSet = setOf(R.id.waterFragment, R.id.waterLogFragment, R.id.settingFragment)

    override fun initView() {
        setSupportActionBar(dataBinding.toolbar)
        val nav = findNavController(R.id.fragment_container_view)
        val navGraph = nav.navInflater.inflate(R.navigation.nav_graph)

        nav.addOnDestinationChangedListener { _, destination, _ ->
            dataBinding.bottomNav.visibility =
                if(mainFragmentSet.contains(destination.id)) View.VISIBLE
                else View.GONE
        }

        if(MainApplication.sharedPref?.getBoolean(C.FirstInstallFlag, false) == false) {
            navGraph.setStartDestination(R.id.initLanguageFragment)
        } else {
            navGraph.setStartDestination(R.id.waterFragment)
        }
        nav.graph = navGraph

        NavigationUI.setupWithNavController(dataBinding.bottomNav, nav)
        val appBarConfiguration = AppBarConfiguration(mainFragmentSet.plus(R.id.initLanguageFragment))
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