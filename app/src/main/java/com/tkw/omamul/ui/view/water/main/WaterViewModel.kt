package com.tkw.omamul.ui.view.water.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tkw.omamul.base.BaseViewModel
import com.tkw.omamul.base.launch
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


    fun addCount(amount: Int, date: String) {
        viewModelScope.launch {
            val entity = WaterEntity().apply {
                this.amount = amount
                this.date = date
            }
            waterRepository.addCount(entity)
        }
    }

    fun removeCount(obj: WaterEntity) {
        viewModelScope.launch {
            waterRepository.deleteCount(obj)
        }
    }

    fun updateAmount(origin: WaterEntity, amount: Int, date: String) {
        launch {
            val target = WaterEntity().apply {
                this.amount = amount
                this.date = date
            }
            val currentList = waterRepository.getCount()?.dayOfList
            if(currentList != null) {
                val targetIndex = currentList.indexOf(origin)
                if(targetIndex != -1) { //수정하려는 객체가 리스트에 있으면 업데이트
                    waterRepository.updateAmount(origin, target)
                } else {                //없으면 추가
                    waterRepository.addCount(target)
                }
            }

        }
    }
}