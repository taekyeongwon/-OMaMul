package com.tkw.omamul.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.tkw.omamul.data.MainRepositoryImpl
import com.tkw.omamul.ui.viewmodel.WaterViewModel
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
val ViewModelFactory = object: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val handle = extras.createSavedStateHandle()
        return when(modelClass) {
            WaterViewModel::class.java -> WaterViewModel(MainRepositoryImpl(null, null), handle)
            else -> throw IllegalArgumentException("Unknown Class")
        } as T
    }
}