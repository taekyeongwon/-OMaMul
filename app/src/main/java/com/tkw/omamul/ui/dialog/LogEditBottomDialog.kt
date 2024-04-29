package com.tkw.omamul.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tkw.omamul.R
import com.tkw.omamul.databinding.DialogLogEditBinding
import com.tkw.common.BottomExpand
import com.tkw.common.BottomExpandImpl
import com.tkw.common.autoCleared
import com.tkw.domain.model.Water
import com.tkw.omamul.ui.view.water.log.LogViewModel
import com.tkw.util.DateTimeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogEditBottomDialog(
    private val selectedItem: Water? = null
): BottomSheetDialogFragment(), BottomExpand by BottomExpandImpl() {
    private var dataBinding by autoCleared<DialogLogEditBinding>()
    private val viewModel: LogViewModel by activityViewModels()

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
        initView()
        initListener()
    }

    private fun initView() {
        if(selectedItem != null) {
            with(dataBinding) {
                etWaterAmount.setText(selectedItem.amount.toString())
                tpDate.hour = selectedItem.getHourFromDate()
                tpDate.minute = selectedItem.getMinuteFromDate()
            }
        }
    }

    private fun initListener() {
        dataBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        dataBinding.btnSave.setOnClickListener {
            val amount = dataBinding.etWaterAmount.text.toString().toInt()
            val fullDateFormat = DateTimeUtils.getFullFormatFromDateTime(
                getSelectedDateTime(),
                dataBinding.tpDate.hour,
                dataBinding.tpDate.minute
            )
            if(selectedItem != null) {
                viewModel.updateAmount(selectedItem, amount, fullDateFormat)
            } else {
                viewModel.addAmount(amount, fullDateFormat)
            }

            dismiss()
        }
    }

    private fun getSelectedDateTime(): String {
        return selectedItem?.dateTime
            ?: DateTimeUtils.getFullFormatFromDate(viewModel.dateLiveData.value)
    }
}