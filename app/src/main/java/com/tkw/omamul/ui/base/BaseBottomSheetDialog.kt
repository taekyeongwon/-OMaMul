package com.tkw.omamul.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tkw.omamul.R

abstract class BaseBottomSheetDialog<VB: ViewDataBinding, VM: BaseViewModel>
    (private val layoutId: Int): BottomSheetDialogFragment() {
    protected abstract val viewModel: VM
    private var _dataBinding: VB? = null
    protected val dataBinding get() = _dataBinding!!

    abstract fun initView()
    abstract fun bindViewModel(binding: VB)
    abstract fun initObserver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from(bottomSheetInternal).state =
                    BottomSheetBehavior.STATE_EXPANDED
            }
        }
        _dataBinding = DataBindingUtil.inflate(layoutInflater, layoutId, container, false)
        return _dataBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        initView()
        initObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _dataBinding = null
    }

    private fun initBinding() {
        bindViewModel(dataBinding)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        dataBinding.executePendingBindings()
    }
}