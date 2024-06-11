package com.tkw.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tkw.common.autoCleared
import com.tkw.ui.databinding.CustomBottomDialogBinding

/**
 * 해당 클래스 상속 시 onCreateView 재정의
 * return super.onCreateView(inflater, container, savedInstanceState) 해줘야 함.
 */
abstract class CustomBottomDialog<T: ViewBinding>: BottomSheetDialogFragment(), BottomExpand by BottomExpandImpl() {
    private var dataBinding by autoCleared<CustomBottomDialogBinding>()
    abstract var childBinding: T

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        onSetBottomBehavior(dialog)
        dataBinding = CustomBottomDialogBinding.inflate(inflater, container, false)

        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView(childBinding.root)
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