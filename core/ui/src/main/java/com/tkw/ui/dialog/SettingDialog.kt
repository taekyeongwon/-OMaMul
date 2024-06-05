package com.tkw.ui.dialog

import android.os.Bundle
import android.view.View

class SettingDialog(
    private val cancelAction: () -> Unit = {},
    private val confirmAction: () -> Unit = {}
): CustomDialog() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        setTextView(
            getString(com.tkw.ui.R.string.permission_title),
            getString(com.tkw.ui.R.string.permission_message)
        )
        setButtonListener(
            cancelButtonTitle = getString(com.tkw.ui.R.string.cancel),
            confirmButtonTitle = getString(com.tkw.ui.R.string.ok),
            cancelAction = {
                cancelAction()
                dismiss()
            },
            confirmAction = {
                confirmAction()
                dismiss()
            }
        )
    }
}