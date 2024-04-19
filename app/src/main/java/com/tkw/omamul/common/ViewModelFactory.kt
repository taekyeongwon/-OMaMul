package com.tkw.omamul.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.tkw.database.local.CupDaoImpl
import com.tkw.data.local.CupRepositoryImpl
import com.tkw.database.local.WaterDaoImpl
import com.tkw.data.local.WaterRepositoryImpl
import com.tkw.domain.model.Cup
import com.tkw.omamul.ui.view.init.InitViewModel
import com.tkw.omamul.ui.view.water.cup.CupViewModel
import com.tkw.omamul.ui.view.water.home.WaterViewModel
import com.tkw.omamul.ui.view.water.log.LogViewModel
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
fun <V> getViewModelFactory(params: V?) = object: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val handle = extras.createSavedStateHandle()

        return when(modelClass) {
            WaterViewModel::class.java -> WaterViewModel(
                WaterRepositoryImpl(WaterDaoImpl()),
                CupRepositoryImpl(CupDaoImpl()),
                handle
            )
            InitViewModel::class.java -> InitViewModel(
                WaterRepositoryImpl(WaterDaoImpl())
            )
            CupViewModel::class.java -> CupViewModel(
                CupRepositoryImpl(CupDaoImpl()),
                params as? Cup ?: Cup()
            )
            LogViewModel::class.java -> LogViewModel(
                WaterRepositoryImpl(WaterDaoImpl())
            )
            else -> throw IllegalArgumentException("Unknown Class")
        } as T
    }
}