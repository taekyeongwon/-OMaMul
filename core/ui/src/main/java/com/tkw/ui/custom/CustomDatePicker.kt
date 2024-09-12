package com.tkw.ui.custom

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.time.LocalDate

class CustomDatePicker(
    private val date: LocalDate,
    private val resultListener: (LocalDate) -> Unit
): DialogFragment(), OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(
            requireActivity(),
            this,
            date.year,
            date.monthValue - 1,
            date.dayOfMonth
        )
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        resultListener(LocalDate.of(year, month + 1, dayOfMonth))
    }
}