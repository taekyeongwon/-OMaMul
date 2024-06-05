package com.tkw.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tkw.common.autoCleared
import com.tkw.ui.databinding.CustomBottomDialogBinding

open class CustomBottomDialog: BottomSheetDialogFragment(), BottomExpand by BottomExpandImpl() {
    private var dataBinding by autoCleared<CustomBottomDialogBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        onSetBottomBehavior(dialog)
        dataBinding = CustomBottomDialogBinding.inflate(inflater, container, false)

        return dataBinding.root
    }

    fun setView(view: View, isOneButton: Boolean = false) {
        dataBinding.llParent.addView(view)
        if(isOneButton) {
            dataBinding.btnCancel.visibility = View.GONE
            dataBinding.divider.visibility = View.GONE
        }
    }

    fun setButtonListener(
        cancelAction: () -> Unit = {},
        confirmAction: () -> Unit = {}
    ) {
        dataBinding.btnCancel.setOnClickListener { cancelAction() }
        dataBinding.btnSave.setOnClickListener { confirmAction() }
    }
}