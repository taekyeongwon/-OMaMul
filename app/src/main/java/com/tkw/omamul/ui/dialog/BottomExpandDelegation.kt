package com.tkw.omamul.ui.dialog

import android.app.Dialog
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

interface BottomExpandDelegation {
    fun onSetBottomBehavior(dialog: Dialog?)
}

class BottomExpandDelegationImpl: BottomExpandDelegation {
    override fun onSetBottomBehavior(dialog: Dialog?) {
        dialog?.setOnShowListener {
            val d = it as BottomSheetDialog
            val bottomSheetInternal = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheetInternal != null) {
                BottomSheetBehavior.from(bottomSheetInternal).state =
                    BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
}