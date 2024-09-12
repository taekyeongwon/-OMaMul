package com.tkw.ui.dialog

import android.app.Dialog
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
            d.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }
}