package com.tkw.ui

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.tkw.common.autoCleared
import com.tkw.ui.databinding.CustomDialogViewBinding

open class CustomDialog: DialogFragment(), DialogResize by DialogResizeImpl() {
    private var dataBinding by autoCleared<CustomDialogViewBinding>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = CustomDialogViewBinding.inflate(layoutInflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dataBinding.root
    }

    override fun onResume() {
        super.onResume()
        onResize(this, 0.9f)
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