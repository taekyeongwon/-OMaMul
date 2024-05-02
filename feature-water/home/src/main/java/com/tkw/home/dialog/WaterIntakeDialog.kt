package com.tkw.home.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.tkw.home.WaterViewModel
import com.tkw.common.DialogResize
import com.tkw.common.DialogResizeImpl
import com.tkw.common.autoCleared
import com.tkw.home.databinding.DialogWaterIntakeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WaterIntakeDialog : DialogFragment(), DialogResize by DialogResizeImpl() {
    private var dataBinding by autoCleared<DialogWaterIntakeBinding>()
    private val viewModel: WaterViewModel by activityViewModels()

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
        initListener()
    }

    override fun onResume() {
        super.onResume()
        onResize(this, 0.9f)
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