package com.tkw.omamul

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.tkw.base.C
import com.tkw.omamul.databinding.ActivityWaterBinding
import com.tkw.home.WaterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WaterActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityWaterBinding
    private val viewModel: WaterViewModel by viewModels()
    private val mainFragmentSet = setOf(
        com.tkw.home.R.id.waterFragment,
        com.tkw.record.R.id.waterLogFragment,
        com.tkw.setting.R.id.settingFragment
    )

    private val broadcastReceiver = DateChangeReceiver {
        viewModel.setToday()
    }
    private val receiveFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        initBinding()
        initView()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, receiveFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    private fun initBinding() {
        dataBinding = ActivityWaterBinding.inflate(layoutInflater)
        dataBinding.run {
            lifecycleOwner = this@WaterActivity
            viewModel = this@WaterActivity.viewModel
            executePendingBindings()
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

        val appBarConfiguration = AppBarConfiguration(mainFragmentSet.plus(com.tkw.init.R.id.initLanguageFragment))
        NavigationUI.setupWithNavController(dataBinding.toolbar, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(dataBinding.bottomNav, navController)

        lifecycleScope.launch {
            setStartDestination(navController)
        }
    }

    private suspend fun setStartDestination(nav: NavController) {
        val navGraph = nav.navInflater.inflate(R.navigation.nav_graph)

        if(viewModel.getInitFlag()) {
            navGraph.setStartDestination(com.tkw.home.R.id.home_nav_graph)
        } else {
            navGraph.setStartDestination(com.tkw.init.R.id.init_nav_graph)
        }
        nav.graph = navGraph
    }
}