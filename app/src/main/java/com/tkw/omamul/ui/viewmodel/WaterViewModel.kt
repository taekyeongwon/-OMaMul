package com.tkw.omamul.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.tkw.omamul.data.MainRepository
import com.tkw.omamul.ui.base.BaseViewModel

class WaterViewModel(
    private val repository: MainRepository,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {

}