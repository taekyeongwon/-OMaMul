package com.tkw.omamul.ui.view.water.main

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.tkw.omamul.MainApplication
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.util.C
import com.tkw.omamul.databinding.ActivityWaterBinding

class WaterActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityWaterBinding
    private val viewModel: WaterViewModel by viewModels { ViewModelFactory }
    private val mainFragmentSet = setOf(
        R.id.waterFragment,
        R.id.waterLogFragment,
        R.id.settingFragment
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        initBinding()
        initView()
    }

    private fun initBinding() {
        dataBinding = ActivityWaterBinding.inflate(layoutInflater)
        dataBinding.run {
            lifecycleOwner = this@WaterActivity
            executePendingBindings()
            viewModel = this@WaterActivity.viewModel
        }
    }

    private fun initView() {
        setContentView(dataBinding.root)
        setSupportActionBar(dataBinding.toolbar)
        initNavigate()
//        viewModel.getCount()
    }

    private fun initNavigate() {
        val navController = findNavController(R.id.fragment_container_view)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            dataBinding.bottomNav.visibility =
                if(mainFragmentSet.contains(destination.id)) View.VISIBLE
                else View.GONE
        }
        setStartDestination(navController)

        val appBarConfiguration = AppBarConfiguration(mainFragmentSet.plus(R.id.initLanguageFragment))
        NavigationUI.setupWithNavController(dataBinding.toolbar, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(dataBinding.bottomNav, navController)
    }

    private fun setStartDestination(nav: NavController) {
        val navGraph = nav.navInflater.inflate(R.navigation.nav_graph)

        if(MainApplication.sharedPref?.getBoolean(C.FirstInstallFlag, false) == false) {
            navGraph.setStartDestination(R.id.initLanguageFragment)
        } else {
            navGraph.setStartDestination(R.id.waterFragment)
        }
        nav.graph = navGraph
    }
}