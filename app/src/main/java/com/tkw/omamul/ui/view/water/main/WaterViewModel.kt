package com.tkw.omamul.ui.view.water.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tkw.omamul.base.BaseViewModel
import com.tkw.omamul.data.WaterRepository
import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import kotlinx.coroutines.launch

class WaterViewModel(
    private val waterRepository: WaterRepository,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {

    val countStreamLiveData: LiveData<DayOfWaterEntity> =
        waterRepository.getCountByFlow().asLiveData()


    fun addCount() {
        viewModelScope.launch {
            waterRepository.updateCount()
        }
    }

    fun removeCount(obj: WaterEntity) {
        viewModelScope.launch {
            waterRepository.deleteCount(obj)
        }
    }
}