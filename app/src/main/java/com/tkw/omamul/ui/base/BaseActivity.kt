package com.tkw.omamul.ui.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB: ViewBinding, VM: BaseViewModel>
    (private val layoutId: Int): AppCompatActivity() {

    protected abstract val viewModel: VM
    protected val dataBinding: VB by lazy {
        DataBindingUtil.setContentView(this, layoutId)
    }

    protected abstract val isSplash: Boolean
    protected abstract fun initView()
    protected abstract fun initObserver()
    protected abstract fun initListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(isSplash) installSplashScreen()
        initView()
        initBaseObserver()
        initListener()
    }

    private fun initBaseObserver() {
        //todo alert, progress observe
        initObserver()
    }

    protected fun goTo(intent: Intent) {
        startActivity(intent)
    }
}