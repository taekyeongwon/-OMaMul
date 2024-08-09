package com.tkw.omamul

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.tkw.alarm.WaterAlarmViewModel
import com.tkw.alarmnoti.NotificationManager
import com.tkw.common.LocaleHelper
import com.tkw.omamul.databinding.ActivityWaterBinding
import com.tkw.home.WaterViewModel
import com.tkw.record.LogViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WaterActivity : AppCompatActivity() {
    private lateinit var dataBinding: ActivityWaterBinding
    private val waterViewModel: WaterViewModel by viewModels()
    private val logViewModel: LogViewModel by viewModels()
    private val alarmViewModel: WaterAlarmViewModel by viewModels()
    private val mainFragmentSet = setOf(
        com.tkw.home.R.id.waterFragment,
        com.tkw.record.R.id.waterLogFragment,
        com.tkw.setting.R.id.settingFragment
    )
    private val hideTitleFragmentSet = setOf(
        com.tkw.init.R.id.initLanguageFragment,
        com.tkw.init.R.id.initIntakeFragment,
        com.tkw.init.R.id.initTimeFragment,
        com.tkw.record.R.id.waterLogFragment,
        com.tkw.setting.R.id.settingFragment
    )
    private var prevAmount: Int = 0

    private val broadcastReceiver = DateChangeReceiver {
        waterViewModel.setToday()
        logViewModel.setToday()
    }
    private val receiveFilter = IntentFilter(Intent.ACTION_DATE_CHANGED)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initLanguage()
        installSplashScreen()
        initBinding()
        initView()
        initObserver()
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, receiveFilter)
        alarmViewModel.setNotificationEnabled(NotificationManager.isNotificationEnabled(this))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
    }

    //최초 설치 시 언어 선택하면 액티비티 재생성되지 않도록(configChanges 적용 안됨) 추가함.
    private fun initLanguage() {
        val getLanguage = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        LocaleHelper.setApplicationLocales(this, getLanguage)
    }

    private fun initBinding() {
        dataBinding = ActivityWaterBinding.inflate(layoutInflater)
    }

    private fun initView() {
        setContentView(dataBinding.root)
        setSupportActionBar(dataBinding.toolbar)
        initNavigate()
    }

    private fun initObserver() {
        waterViewModel.amountLiveData.observe(this) {
            lifecycleScope.launch {
                val prev = prevAmount
                if(it.getTotalWaterAmount() >= waterViewModel.getIntakeAmount()) {
                    if(prev < waterViewModel.getIntakeAmount()) {
                        Toast.makeText(
                            this@WaterActivity,
                            getString(com.tkw.ui.R.string.intake_complete),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    alarmViewModel.saveReachedGoal(true)
                } else {
                    alarmViewModel.saveReachedGoal(false)
                }
                prevAmount = it.getTotalWaterAmount()
            }
        }
        alarmViewModel.isReachedGoal.observe(this) {
            lifecycleScope.launch {
                val isNotificationEnabled = alarmViewModel.isNotificationAlarmEnabled().first()
                alarmViewModel.delayAllAlarm(it, isNotificationEnabled)
            }
        }
    }

    private fun initNavigate() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener { _, destination, _ ->
            dataBinding.bottomNav.visibility =
                if(mainFragmentSet.contains(destination.id)) View.VISIBLE
                else View.GONE
            //최초 진입 화면, 로그, 설정 화면은 타이틀 안보이게 처리
            if(hideTitleFragmentSet.contains(destination.id)) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
        }

        val appBarConfiguration = AppBarConfiguration(mainFragmentSet.plus(com.tkw.init.R.id.initLanguageFragment))
        NavigationUI.setupWithNavController(dataBinding.toolbar, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(dataBinding.bottomNav, navController)

        dataBinding.toolbar.setNavigationOnClickListener {
            // 시스템 back key 동작과 동일하게 설정.
            // CupManageFragment 등 특정 flag에서 백 키 또는 업 버튼 눌렀을 때 백스택 이동 제어하기 위해 설정.
            // 다른 프래그먼트에서 onBackPressedDispatcher에 콜백을 설정함으로써 백스택 이동을 제어할 수 있음.
            onBackPressedDispatcher.onBackPressed()
        }

        lifecycleScope.launch {
            setStartDestination(navController)
        }
    }

    private suspend fun setStartDestination(nav: NavController) {
        val navGraph = nav.navInflater.inflate(R.navigation.nav_graph)

        if(waterViewModel.getInitFlag()) {
            navGraph.setStartDestination(com.tkw.home.R.id.home_nav_graph)
        } else {
            navGraph.setStartDestination(com.tkw.init.R.id.init_nav_graph)
        }
        nav.graph = navGraph
    }
}