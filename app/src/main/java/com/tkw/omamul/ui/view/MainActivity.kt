package com.tkw.omamul.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupWithNavController
import com.tkw.omamul.MainApplication
import com.tkw.omamul.R
import com.tkw.omamul.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)


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

        NavigationUI.setupWithNavController(binding.bottomNav, nav)
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.WaterFragment, R.id.waterLogFragment, R.id.settingFragment))
        NavigationUI.setupWithNavController(binding.toolbar, nav, appBarConfiguration)
    }
}