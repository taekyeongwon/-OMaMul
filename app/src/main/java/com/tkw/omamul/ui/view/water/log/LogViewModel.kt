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

    //주간 날짜
    private val weekStringFlow = MutableStateFlow(dateStringFlow.value)
    val weekLiveData = weekStringFlow.asLiveData()

    //월간 날짜
    private val monthStringFlow = MutableStateFlow(dateStringFlow.value)
    val monthLiveData = monthStringFlow.asLiveData()

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
                    if(currentIndex > 0) {
                        //현재 세팅된 날짜 이전의 날짜 값으로 세팅
                        dateStringFlow.value = getCurrentDayOfWaterList().list[currentIndex - 1].date
                        getDayAmount()
                    }
                }
                LogContract.Move.RIGHT -> {
                    val currentIndex = getCurrentDateWaterIndex()
                    if(currentIndex < getCurrentDayOfWaterList().list.size - 1) {
                        //현재 세팅된 날짜보다 앞의 날짜 값으로 세팅
                        dateStringFlow.value = getCurrentDayOfWaterList().list[currentIndex + 1].date
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
                    val currentIndex = getCurrentWeekIndex()
                    if(currentIndex > 0) {
                        weekStringFlow.value =
                            getCurrentDayOfWaterList()
                                .getWeekArray()[currentIndex - 1]
                                .first
                        getWeekAmount()
                    }
                }
                LogContract.Move.RIGHT -> {
                    val currentIndex = getCurrentWeekIndex()
                    if(currentIndex < getCurrentDayOfWaterList().getWeekArray().size - 1) {
                        weekStringFlow.value =
                            getCurrentDayOfWaterList()
                                .getWeekArray()[currentIndex + 1]
                                .first
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
        waterRepository.getAmountWeekByFlow(weekStringFlow.value).map { list ->
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
        launch {
            when(move) {
                LogContract.Move.LEFT -> {
                    val currentIndex = getCurrentMonthIndex()
                    if(currentIndex > 0) {
                        monthStringFlow.value =
                            getCurrentDayOfWaterList()
                                .getWeekArray()[currentIndex - 1]
                                .first
                        getMonthAmount()
                    }
                }
                LogContract.Move.RIGHT -> {
                    val currentIndex = getCurrentMonthIndex()
                    if(currentIndex < getCurrentDayOfWaterList().getMonthArray().size - 1) {
                        monthStringFlow.value =
                            getCurrentDayOfWaterList()
                                .getWeekArray()[currentIndex + 1]
                                .first
                        getMonthAmount()
                    }
                }
                LogContract.Move.INIT -> {
                    getMonthAmount()
                }
            }
        }
    }

    private suspend fun getMonthAmount() {
        waterRepository.getAmountMonthByFlow(monthStringFlow.value).map { list ->
            DayOfWaterList(
                list.map {
                    it.toMap()
                }
            )
        }.catch {
            setState { LogContract.State.Error }
        }.collect {
            if(it.list.isNotEmpty()) {
                setState { LogContract.State.Complete(it, LogContract.DateUnit.MONTH) }
            } else {
                setState { LogContract.State.Error }
            }
        }
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

    private fun getCurrentDayOfWaterList(): DayOfWaterList = allDayOfWaterLiveData.value

    //List<DayOfWater>에서 현재 세팅된 날짜랑 같은 마지막 DayOfWater의 인덱스
    private fun getCurrentDateWaterIndex(): Int {
        return getCurrentDayOfWaterList().list
            .indexOfLast { it.date == dateStringFlow.value }
    }

    private fun getCurrentWeekIndex(): Int {
        return getCurrentDayOfWaterList().getWeekArray().indexOfFirst {
            it.first <= weekStringFlow.value
                    && weekStringFlow.value <= it.second
        }
    }

    private fun getCurrentMonthIndex(): Int {
        return getCurrentDayOfWaterList().getMonthArray().indexOfFirst {
            it.first <= monthStringFlow.value
                    && monthStringFlow.value <= it.second
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