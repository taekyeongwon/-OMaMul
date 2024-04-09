package com.tkw.omamul.ui.view.water.log

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tkw.omamul.base.IntentBaseViewModel
import com.tkw.omamul.base.launch
import com.tkw.omamul.common.util.DateTimeUtils
import com.tkw.omamul.data.WaterRepository
import com.tkw.omamul.data.model.DayOfWater
import com.tkw.omamul.data.model.DayOfWaterList
import com.tkw.omamul.data.model.Water
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

class LogViewModel(
    private val waterRepository: WaterRepository
): IntentBaseViewModel
<LogContract.Event, LogContract.State, LogContract.SideEffect>() {

    //현재 날짜
    private val dateStringFlow = MutableStateFlow(DateTimeUtils.getTodayDate())
    val dateLiveData = dateStringFlow.asLiveData()

    //전체 DayOfWater 리스트
    @OptIn(ExperimentalCoroutinesApi::class)
    private val allDayOfWaterLiveData: StateFlow<DayOfWaterList> =
        waterRepository.getAllDayEntity().mapLatest { list ->
            DayOfWaterList(
                list.map { it.toMap() }
            )
        }.stateIn(
            initialValue = DayOfWaterList(arrayListOf()),
            started = SharingStarted.Eagerly,
            scope = viewModelScope
        )

    override fun createInitialState(): LogContract.State {
        return LogContract.State.Loading(false)
    }

    override fun handleEvent(event: LogContract.Event) {
        when(event) {
            is LogContract.Event.DayAmountEvent -> dayAmountEvent(event.move)
            is LogContract.Event.WeekAmountEvent -> weekAmountEvent(event.move)
            is LogContract.Event.MonthAmountEvent -> monthAmountEvent(event.move)
            LogContract.Event.ShowAddDialog -> showAddDialog()
            is LogContract.Event.ShowEditDialog -> showEditDialog(event.water)
            is LogContract.Event.RemoveDayAmount -> removeDayAmount(event.water)
        }
    }

    private fun dayAmountEvent(move: LogContract.Move) {
        launch {
            when(move) {
                LogContract.Move.LEFT -> {
                    val currentIndex = getCurrentDateWaterIndex()
                    if(currentIndex > 0 && getCurrentDayOfWaterList().isNotEmpty()) {
                        //현재 세팅된 날짜 이전의 날짜 값으로 세팅
                        dateStringFlow.value = getCurrentDayOfWaterList()[currentIndex - 1].date
                        getDayAmount()
                    }
                }
                LogContract.Move.RIGHT -> {
                    val currentIndex = getCurrentDateWaterIndex()
                    if(currentIndex < getCurrentDayOfWaterList().size - 1) {
                        //현재 세팅된 날짜보다 앞의 날짜 값으로 세팅
                        dateStringFlow.value = getCurrentDayOfWaterList()[currentIndex + 1].date
                        getDayAmount()
                    }
                }
                LogContract.Move.INIT -> {
                    getDayAmount()
                }
            }
        }
    }

    //현재 날짜로 조회한 DayOfWater
    private suspend fun getDayAmount() {
        waterRepository.getAmountByFlow(dateStringFlow.value).map { it.toMap() }
            .catch {
                setState { LogContract.State.Error }
            }
            .collect {
                val list = DayOfWaterList(arrayListOf(it))
                setState { LogContract.State.Complete(list, LogContract.DateUnit.DAY) }
            }
    }

    private fun weekAmountEvent(move: LogContract.Move) {
        launch {
            when(move) {
                LogContract.Move.LEFT -> {
                    val minusWeekDate = DateTimeUtils.minusWeek(dateStringFlow.value)
                    if(getCurrentWeekWaterIndex(minusWeekDate) != -1) {
                        dateStringFlow.value = minusWeekDate
                        getWeekAmount()
                    }
                }
                LogContract.Move.RIGHT -> {
                    val plusWeekDate = DateTimeUtils.plusWeek(dateStringFlow.value)
                    if(getCurrentWeekWaterIndex(plusWeekDate) != -1) {
                        dateStringFlow.value = plusWeekDate
                        getWeekAmount()
                    }
                }
                LogContract.Move.INIT -> {
                    getWeekAmount()
                }
            }
        }
    }

    //현재 날짜에 해당하는 주의 월요일 ~ 일요일까지의 DayOfWater 리스트
    private suspend fun getWeekAmount() {
        waterRepository.getAmountWeekByFlow(dateStringFlow.value).map { list ->
            DayOfWaterList(
                list.map {
                    it.toMap()
                }
            )
        }.catch {
            setState { LogContract.State.Error }
        }.collect {
            if(it.list.isNotEmpty()) {
                setState { LogContract.State.Complete(it, LogContract.DateUnit.WEEK) }
            } else {
                setState { LogContract.State.Error }
            }
        }
    }

    private fun monthAmountEvent(move: LogContract.Move) {

    }

    private fun showAddDialog() {
        setSideEffect { LogContract.SideEffect.ShowEditDialog(null) }
    }

    private fun showEditDialog(item: Water) {
        setSideEffect { LogContract.SideEffect.ShowEditDialog(item) }
    }

    private fun removeDayAmount(water: Water) {
        launch {
            waterRepository.deleteAmount(dateStringFlow.value, water.dateTime)
        }
    }

    private fun getCurrentDayOfWaterList(): List<DayOfWater> = allDayOfWaterLiveData.value.list

    //List<DayOfWater>에서 현재 세팅된 날짜랑 같은 마지막 DayOfWater의 인덱스
    private fun getCurrentDateWaterIndex(): Int {
        return getCurrentDayOfWaterList()
            .indexOfLast { it.date == dateStringFlow.value }
    }

    private fun getCurrentWeekWaterIndex(date: String): Int {
        val week = DateTimeUtils.getWeekDates(date)
        return getCurrentDayOfWaterList().indexOfFirst {
            week.first <= it.date && it.date <= week.second
        }
    }

    //LogEditBottomDialog에서 사용할 메서드
    fun addAmount(amount: Int, date: String) {
        launch {
            waterRepository.addAmount(dateStringFlow.value, amount, date)
        }
    }

    fun updateAmount(origin: Water, amount: Int, date: String) {
        launch {
            waterRepository.updateAmount(dateStringFlow.value, origin, amount, date)
        }
    }
}