package com.tkw.home.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.tkw.home.WaterViewModel
import com.tkw.ui.DialogResize
import com.tkw.ui.DialogResizeImpl
import com.tkw.common.autoCleared
import com.tkw.home.databinding.DialogWaterIntakeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
        initView()
        initObserver()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        onResize(this, 0.9f)
    }

    private fun initView() {
        lifecycleScope.launch {
            val current = dataBinding.npAmount.getCurrentValue()
            dataBinding.npAmount.setValue(viewModel.getIntakeAmount(current))
        }
    }

    private fun initObserver() {
        viewModel.amountSaveEvent.observe(viewLifecycleOwner) {
            dismiss()
        }
    }

    private fun initListener() {
        dataBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        dataBinding.btnSave.setOnClickListener {
            val amount = dataBinding.npAmount.getCurrentValue()
            viewModel.saveIntakeAmount(amount)
        }
    }
}