package com.tkw.omamul.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.DialogWaterIntakeBinding
import com.tkw.omamul.ui.view.water.main.WaterViewModel
import com.tkw.omamul.util.DialogResize
import com.tkw.omamul.util.DialogResizeImpl
import com.tkw.omamul.util.autoCleared

class WaterIntakeDialog: DialogFragment(), DialogResize by DialogResizeImpl() {
    private var dataBinding by autoCleared<DialogWaterIntakeBinding>()
    private val viewModel: WaterViewModel by activityViewModels { ViewModelFactory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DialogWaterIntakeBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        onResize(this, 0.9f)
    }
}