package com.tkw.omamul.ui.view.water.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.tkw.omamul.base.BaseViewModel
import com.tkw.omamul.base.launch
import com.tkw.omamul.data.WaterRepository
import com.tkw.omamul.data.model.DayOfWater
import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.Water
import com.tkw.omamul.data.model.WaterEntity
import kotlinx.coroutines.launch

class WaterViewModel(
    private val waterRepository: WaterRepository,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {

    val countStreamLiveData: LiveData<DayOfWaterEntity> =
        waterRepository.getCountByFlow().asLiveData()


    fun addCount(amount: Int, date: String) {
        viewModelScope.launch {
            val entity = WaterEntity().apply {
                this.amount = amount
                this.date = date
            }
            waterRepository.addCount(entity)
        }
    }

    fun removeCount(obj: Water) {
        viewModelScope.launch {
            val currentList = countStreamLiveData.value?.dayOfList
            if(currentList != null) {
                val currentItem = currentList.find { it.date == obj.date }
                if(currentItem != null) {
                    waterRepository.deleteCount(currentItem)
                }
            }
        }
    }

    fun updateAmount(origin: Water, amount: Int, date: String) {
        launch {
            val target = WaterEntity().apply {
                this.amount = amount
                this.date = date
            }
            val currentList = countStreamLiveData.value?.dayOfList
            if(currentList != null) {
                //Water 객체의 date와 같은 WaterEntity 객체 가져오기
                val currentItem = currentList.find { it.date == origin.date }
                if(currentItem != null) { //수정하려는 객체가 리스트에 있으면 업데이트
                    waterRepository.updateAmount(currentItem, target)
                } else {                //없으면 추가
                    waterRepository.addCount(target)
                }
            }

        }
    }
}