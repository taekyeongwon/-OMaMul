package com.tkw.record.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.tkw.common.autoCleared
import com.tkw.common.util.DateTimeUtils
import com.tkw.domain.model.Water
import com.tkw.record.LogViewModel
import com.tkw.record.databinding.DialogLogEditBinding
import com.tkw.ui.dialog.CustomBottomDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogEditBottomDialog(
    private val selectedItem: Water? = null
): CustomBottomDialog<DialogLogEditBinding>() {
    override var childBinding by autoCleared<DialogLogEditBinding>()
    private val viewModel: LogViewModel by activityViewModels()
    override var buttonCount: Int = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        childBinding = DialogLogEditBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
    }

    private fun initView() {
        if(selectedItem != null) {
            with(childBinding) {
                etWaterAmount.setText(selectedItem.amount.toString())
                tpDate.hour = selectedItem.getHourFromDate()
                tpDate.minute = selectedItem.getMinuteFromDate()
            }
        }
    }

    private fun initListener() {
        setButtonListener(
            cancelAction = {
                dismiss()
            },
            confirmAction = {
                val amount = childBinding.etWaterAmount.text.toString().toInt()
                val fullDateFormat = DateTimeUtils.getFullFormatFromDateTime(
                    getSelectedDateTime(),
                    childBinding.tpDate.hour,
                    childBinding.tpDate.minute
                )
                if(selectedItem != null) {
                    viewModel.updateAmount(selectedItem, amount, fullDateFormat)
                } else {
                    viewModel.addAmount(amount, fullDateFormat)
                }

                dismiss()
            }
        )
    }

    private fun getSelectedDateTime(): String {
        return selectedItem?.dateTime
            ?: DateTimeUtils.getFullFormatFromDate(viewModel.getSelectedDate())
    }
}