package com.tkw.omamul.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.DialogLogEditBinding
import com.tkw.omamul.ui.base.BaseBottomSheetDialog
import com.tkw.omamul.ui.view.water.main.WaterViewModel

class LogEditDialog: BaseBottomSheetDialog<DialogLogEditBinding, WaterViewModel>
    (R.layout.dialog_log_edit) {
    override val viewModel: WaterViewModel by viewModels { ViewModelFactory }

    override fun getTheme(): Int {
        return R.style.DialogStyle
    }

    override fun initView() {

    }

    override fun bindViewModel(binding: DialogLogEditBinding) {

    }

    override fun initObserver() {

    }
}