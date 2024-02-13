package com.tkw.omamul.ui.base

import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheetDialog<VB: ViewBinding, VM: BaseViewModel>
    (private val layoutId: Int): BottomSheetDialogFragment() {
}