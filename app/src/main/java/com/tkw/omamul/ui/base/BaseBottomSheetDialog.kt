package com.tkw.omamul.ui.base

import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialog<VB: ViewDataBinding, VM: BaseViewModel>
    (private val layoutId: Int): BottomSheetDialogFragment() {
}