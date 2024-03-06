package com.tkw.omamul.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.DialogLogEditBinding
import com.tkw.omamul.ui.view.water.main.WaterViewModel
import com.tkw.omamul.util.autoCleared

class LogEditBottomDialog: BottomSheetDialogFragment(),
    BottomExpandDelegation by BottomExpandDelegationImpl() {
    private var dataBinding by autoCleared<DialogLogEditBinding>()
    private val viewModel: WaterViewModel by viewModels { ViewModelFactory }

    override fun getTheme(): Int {
        return R.style.BottomDialogStyle
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        onSetBottomBehavior(dialog)
        dataBinding = DialogLogEditBinding.inflate(inflater, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListener()
    }

    private fun initListener() {
        dataBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        dataBinding.btnSave.setOnClickListener {
            //todo 데이터 저장하고 dismiss 시 해당 데이터 보여지도록 해야 함.
            dismiss()
        }
    }
}