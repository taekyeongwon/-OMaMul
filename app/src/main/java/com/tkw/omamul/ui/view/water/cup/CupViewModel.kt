package com.tkw.omamul.ui.view.water.cup

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tkw.omamul.base.BaseViewModel
import com.tkw.omamul.base.launch
import com.tkw.omamul.data.CupRepository
import com.tkw.omamul.data.model.Cup
import com.tkw.omamul.data.model.CupEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

class CupViewModel(
    private val cupRepository: CupRepository,
    private val params: Cup
): BaseViewModel() {

    private val cupListFlow: StateFlow<List<CupEntity>> =
        cupRepository.getCupList().stateIn(
            initialValue = arrayListOf(),
            started = SharingStarted.WhileSubscribed(5000),
            scope = viewModelScope
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val cupListLiveData: LiveData<List<Cup>> =
        cupListFlow.mapLatest {
            it.map { cup ->
                cup.toMap()
            }
        }.asLiveData()

    fun insertCup(cup: Cup) {
        launch {
            val target = CupEntity().apply {
                cupId = cup.cupId
                cupName = cup.cupName
                cupAmount = cup.cupAmount
            }
            cupRepository.insertCup(target)
        }
    }

    fun updateCup(cup: Cup) {
        launch {
            val currentList = cupListFlow.value
            val currentItem = currentList.find { it.cupId == cup.cupId }
            if(currentItem != null) {
                cupRepository.updateCup(currentItem.apply {
                    cupName = cup.cupName
                    cupAmount = cup.cupAmount
                })
            }
        }
    }
}