package com.tkw.common

import android.app.Dialog
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

interface BottomExpand {
    fun onSetBottomBehavior(dialog: Dialog?)
}

//bottom sheet dialog 테마 windowSoftInputMode
// - adjustResize 설정 시 키보드 위로 뷰 전부 올라오도록 설정을 위임하는 클래스
class BottomExpandImpl: BottomExpand {
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