package com.tkw.omamul.ui.view.water.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.tkw.base.BaseViewModel
import com.tkw.base.launch
import com.tkw.domain.CupRepository
import com.tkw.domain.WaterRepository
import com.tkw.domain.model.Cup
import com.tkw.domain.model.DayOfWater
import com.tkw.domain.model.Water
import com.tkw.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class WaterViewModel
@Inject constructor(
    private val waterRepository: WaterRepository,
    private val cupRepository: CupRepository,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {

    //현재 날짜
    private val dateStringFlow = MutableStateFlow(DateTimeUtils.getTodayDate())

    //현재 날짜로 조회한 DayOfWater
    @OptIn(ExperimentalCoroutinesApi::class)
    val amountLiveData: LiveData<DayOfWater> = dateStringFlow.flatMapLatest { date ->
        waterRepository.getAmountByFlow(date)
    }.asLiveData()

    //메인화면에 표시할 컵 리스트
    @OptIn(ExperimentalCoroutinesApi::class)
    val cupListLiveData: LiveData<List<Cup>> =
        cupRepository.getCupList().mapLatest {
            it.cupList
        }.asLiveData()

    //컵 관리 화면 이동 후 돌아왔을 때 위치 저장용
    val cupPagerScrollPosition = MutableLiveData(0)

    //date값 변경에 따라 flow에서 새로운 DayOfWater 객체 collect하기 위한 메서드
    fun setDate(date: String) {
        dateStringFlow.value = date
    }

    fun addCount(amount: Int, date: String) {
        launch {
            waterRepository.addAmount(dateStringFlow.value, amount, date)
        }
    }

    fun removeCount(obj: Water) {
        launch {
            waterRepository.deleteAmount(dateStringFlow.value, obj.dateTime)
        }
    }
}