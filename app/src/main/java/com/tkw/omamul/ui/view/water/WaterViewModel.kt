package com.tkw.omamul.ui.view.water

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tkw.omamul.base.BaseViewModel
import com.tkw.omamul.base.launch
import com.tkw.omamul.common.util.DateTimeUtils
import com.tkw.omamul.data.CupRepository
import com.tkw.omamul.data.WaterRepository
import com.tkw.omamul.data.model.Cup
import com.tkw.omamul.data.model.CupEntity
import com.tkw.omamul.data.model.DayOfWater
import com.tkw.omamul.data.model.Water
import com.tkw.omamul.data.model.WaterEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WaterViewModel(
    private val waterRepository: WaterRepository,
    private val cupRepository: CupRepository,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {

    private val amountFlow = MutableStateFlow(DateTimeUtils.getTodayDate())

    @OptIn(ExperimentalCoroutinesApi::class)
    val amountLiveData: LiveData<DayOfWater> = amountFlow.flatMapLatest { date ->
        waterRepository.getAmountByFlow(date).map { it.toMap() }
    }.asLiveData()

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

    //컵 관리 화면 이동 후 돌아왔을 때 위치 저장용
    val cupPagerScrollPosition = MutableLiveData(0)

    //date값 변경에 따라 flow에서 새로운 DayOfWater 객체 collect하기 위한 메서드
    fun setDate(date: String) {
        amountFlow.value = date
    }

    fun addCount(amount: Int, date: String) {
        viewModelScope.launch {
            val entity = WaterEntity().apply {
                this.amount = amount
                this.dateTime = date
            }
            waterRepository.addAmount(amountFlow.value, entity)
        }
    }

    fun removeCount(obj: Water) {
        viewModelScope.launch {
            val current = waterRepository.getWater(amountFlow.value, obj.dateTime)
            if(current != null) {
                waterRepository.deleteAmount(amountFlow.value, current)
            }
        }
    }

    fun updateAmount(origin: Water, amount: Int, date: String) {
        launch {
            val target = WaterEntity().apply {
                this.amount = amount
                this.dateTime = date
            }
            //Water 객체의 date와 같은 WaterEntity 객체 가져오기
            val current = waterRepository.getWater(amountFlow.value, origin.dateTime)
            if(current != null) { //수정하려는 객체가 리스트에 있으면 업데이트
                waterRepository.updateAmount(current, target)
            } else {                //없으면 추가
                waterRepository.addAmount(amountFlow.value, target)
            }
        }
    }
}