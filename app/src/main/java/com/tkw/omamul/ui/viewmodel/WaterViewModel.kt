package com.tkw.omamul.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tkw.omamul.data.MainRepository
import com.tkw.omamul.data.model.CountEntity
import com.tkw.omamul.ui.base.BaseViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WaterViewModel(
    private val repository: MainRepository,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {

    private val _countLiveData = MutableLiveData<Int>()
    val countLiveData: LiveData<Int> get() = _countLiveData

    val countStreamLiveData: LiveData<CountEntity> = repository.getCountByFlow().asLiveData()

    init {
//        getCount()
    }

    fun addCount() {
        viewModelScope.launch {
            repository.addCount()
//            val q = repository.getCount()
//            _countLiveData.value = q
        }
    }

    fun getCount() {
        viewModelScope.launch {
            _countLiveData.value = repository.getCount()
        }
    }

    fun removeCount(obj: CountEntity) {
        viewModelScope.launch {
            repository.removeCount(obj)
        }
    }
}