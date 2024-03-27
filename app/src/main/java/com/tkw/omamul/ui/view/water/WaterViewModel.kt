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
import com.tkw.omamul.data.model.CupList
import com.tkw.omamul.data.model.DayOfWater
import com.tkw.omamul.data.model.Water
import com.tkw.omamul.data.model.WaterEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WaterViewModel(
    private val waterRepository: WaterRepository,
    private val cupRepository: CupRepository,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {

    //현재 날짜
    private val dateStringFlow = MutableStateFlow(DateTimeUtils.getTodayDate())
    val dateLiveData = dateStringFlow.asLiveData()

    //현재 날짜로 조회한 DayOfWater
    @OptIn(ExperimentalCoroutinesApi::class)
    val amountLiveData: LiveData<DayOfWater> = dateStringFlow.flatMapLatest { date ->
        waterRepository.getAmountByFlow(date).map { it.toMap() }
    }.asLiveData()

    //전체 DayOfWater 리스트
    @OptIn(ExperimentalCoroutinesApi::class)
    val allDayOfWaterLiveData: LiveData<List<DayOfWater>> =
        waterRepository.getAllDayEntity().mapLatest { list ->
            list.map { it.toMap() }
        }.asLiveData()

    //메인화면에 표시할 컵 리스트
    @OptIn(ExperimentalCoroutinesApi::class)
    private val cupListFlow: StateFlow<CupList> =
        cupRepository.getCupList().flatMapLatest {
            flow {
                emit(it.toMap())
            }
        }.stateIn(
            initialValue = CupList(),
            started = SharingStarted.WhileSubscribed(5000),
            scope = viewModelScope
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val cupListLiveData: LiveData<List<Cup>> =
        cupListFlow.mapLatest {
            it.cupList
        }.asLiveData()

    //컵 관리 화면 이동 후 돌아왔을 때 위치 저장용
    val cupPagerScrollPosition = MutableLiveData(0)

    //date값 변경에 따라 flow에서 새로운 DayOfWater 객체 collect하기 위한 메서드
    fun setDate(date: String) {
        dateStringFlow.value = date
    }

    fun addCount(amount: Int, date: String) {
        viewModelScope.launch {
            val entity = WaterEntity().apply {
                this.amount = amount
                this.dateTime = date
            }
            waterRepository.addAmount(dateStringFlow.value, entity)
        }
    }

    fun removeCount(obj: Water) {
        viewModelScope.launch {
            val current = waterRepository.getWater(dateStringFlow.value, obj.dateTime)
            if(current != null) {
                waterRepository.deleteAmount(dateStringFlow.value, current)
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
            val current = waterRepository.getWater(dateStringFlow.value, origin.dateTime)
            if(current != null) { //수정하려는 객체가 리스트에 있으면 업데이트
                waterRepository.updateAmount(current, target)
            } else {                //없으면 추가
                waterRepository.addAmount(dateStringFlow.value, target)
            }
        }
    }
}