package com.tkw.omamul.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tkw.omamul.data.WaterRepository
import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import com.tkw.omamul.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class WaterViewModel(
    private val waterRepository: WaterRepository,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {

    private val _countLiveData = MutableLiveData<Int>()
    val countLiveData: LiveData<Int> get() = _countLiveData

    val countStreamLiveData: LiveData<DayOfWaterEntity> = waterRepository.getQueryByFlow().asLiveData()

    init {
//        getCount()
    }

    fun addCount() {
        viewModelScope.launch {
            waterRepository.updateCount()
//            val q = repository.getCount()
//            _countLiveData.value = q
        }
    }

    fun getCount() {
        viewModelScope.launch {
            _countLiveData.value = waterRepository.getCountById()
        }
    }

    fun removeCount(obj: WaterEntity) {
        viewModelScope.launch {
            waterRepository.deleteCount(obj)
        }
    }
}