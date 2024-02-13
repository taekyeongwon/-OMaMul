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

    /**
     * viewBinding을 프래그먼트에서 사용하는 경우 메모리 leak 발생할 가능성이 있음.
     * 프래그먼트 onDestroyView 호출 후 view에 대한 reference를 계속 참조할 수 있기 때문
     * 따라서 onDestroyView에서 binding을 null로 지정해준다.
     */
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