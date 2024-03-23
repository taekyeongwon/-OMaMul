package com.tkw.omamul.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tkw.omamul.R
import com.tkw.omamul.common.ViewModelFactory
import com.tkw.omamul.databinding.DialogLogEditBinding
import com.tkw.omamul.ui.view.water.WaterViewModel
import com.tkw.omamul.common.BottomExpand
import com.tkw.omamul.common.BottomExpandImpl
import com.tkw.omamul.common.autoCleared
import com.tkw.omamul.common.util.DateTimeUtils
import com.tkw.omamul.data.model.Water

class LogEditBottomDialog(
    private val selectedItem: Water = Water()
): BottomSheetDialogFragment(), BottomExpand by BottomExpandImpl() {
    private var dataBinding by autoCleared<DialogLogEditBinding>()
    private val viewModel: WaterViewModel by activityViewModels { ViewModelFactory }

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
        if(selectedItem.dateTime.isNotEmpty()) {
            with(dataBinding) {
                etWaterAmount.setText(selectedItem.amount.toString())
                tpDate.hour =
                    DateTimeUtils.getTimeFromFullFormat(selectedItem.dateTime).hour
                tpDate.minute =
                    DateTimeUtils.getTimeFromFullFormat(selectedItem.dateTime).minute
            }
        }
    }

    private fun initListener() {
        dataBinding.btnCancel.setOnClickListener {
            dismiss()
        }

        dataBinding.btnSave.setOnClickListener {
            val amount = dataBinding.etWaterAmount.text.toString().toInt()
            val date = DateTimeUtils.getFullFormatFromTime(
                selectedItem.dateTime,
                dataBinding.tpDate.hour,
                dataBinding.tpDate.minute
            )
            viewModel.updateAmount(selectedItem, amount, date)
            dismiss()
        }
    }
}