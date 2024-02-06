package com.tkw.omamul.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB: ViewBinding, VM: BaseViewModel> //데이터 바인딩 시 ViewBinding -> ViewDataBinding
    (private val layoutId: Int): Fragment() {

    protected abstract val viewModel: VM
    private var _dataBinding: VB? = null
    protected val dataBinding get() = _dataBinding

    protected abstract fun initView()
    protected abstract fun initObserver()
    protected abstract fun initListener()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _dataBinding = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)
        return _dataBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initBaseObserver()
        initObserver()
        initListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dataBinding = null
    }

    private fun initBaseObserver() {
        //todo alert, progress observe
    }

    protected fun nextFragment() {
        //todo navigation 이동
    }
}