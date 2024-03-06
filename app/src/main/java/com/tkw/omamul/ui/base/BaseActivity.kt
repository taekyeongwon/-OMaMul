package com.tkw.omamul.ui.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB: ViewDataBinding, VM: BaseViewModel>
    (private val layoutId: Int): AppCompatActivity() {

    protected abstract val viewModel: VM
    protected lateinit var dataBinding: VB

    protected abstract val isSplash: Boolean
    protected abstract fun initView()
    protected abstract fun bindViewModel(binder: VB)
    protected abstract fun initObserver()
    protected abstract fun initListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(isSplash) installSplashScreen()
        initBinding()
        initView()
        initBaseObserver()
        initListener()
    }

    private fun initBinding() {
        dataBinding = DataBindingUtil.setContentView(this, layoutId)
        bindViewModel(dataBinding)
        dataBinding.lifecycleOwner = this
        dataBinding.executePendingBindings()
    }

    private fun initBaseObserver() {
        //todo alert, progress observe
        initObserver()
    }

    protected fun goTo(intent: Intent) {
        startActivity(intent)
    }
}