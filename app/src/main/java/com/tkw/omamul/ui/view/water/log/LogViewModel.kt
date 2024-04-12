package com.tkw.omamul.ui.view.water.log

import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tkw.omamul.base.IntentBaseViewModel
import com.tkw.omamul.base.launch
import com.tkw.omamul.common.util.DateTimeUtils
import com.tkw.omamul.data.WaterRepository
import com.tkw.omamul.data.model.DayOfWaterList
import com.tkw.omamul.data.model.Water
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

class LogViewModel(
    private val waterRepository: WaterRepository
): IntentBaseViewModel
<LogContract.Event, LogContract.State, LogContract.SideEffect>() {

    //현재 날짜
    private val dateStateFlow = MutableStateFlow(DateTimeUtils.getTodayDate())
    @OptIn(ExperimentalCoroutinesApi::class)
    private val dateAmountFlow = dateStateFlow.flatMapLatest { date ->
        waterRepository.getAmountByFlow(date).map { it.toMap() }
    }
    val dateLiveData = dateStateFlow.asLiveData()

    //주간 날짜
    private val weekShareFlow = MutableSharedFlow<String>()
    val weekLiveData = weekShareFlow.asLiveData()

    //월간 날짜
    private val monthShareFlow = MutableSharedFlow<String>()
    val monthLiveData = monthShareFlow.asLiveData()

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

    init {
        launch {
            dateAmountFlow.collect {
                val list = DayOfWaterList(arrayListOf(it))
                setState { LogContract.State.Complete(list, LogContract.DateUnit.DAY) }
            }
        }
        launch {
            weekShareFlow.collect {
                getWeekAmount(it)
            }
        }
        launch {
            monthShareFlow.collect {
                getMonthAmount(it)
            }
        }
    }

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
                        dateStateFlow.value = (getCurrentDayOfWaterList().list[currentIndex - 1].date)
                    }
                }
                LogContract.Move.RIGHT -> {
                    val currentIndex = getCurrentDateWaterIndex()
                    if(currentIndex < getCurrentDayOfWaterList().list.size - 1) {
                        //현재 세팅된 날짜보다 앞의 날짜 값으로 세팅
                        dateStateFlow.value = (getCurrentDayOfWaterList().list[currentIndex + 1].date)
                    }
                }
                LogContract.Move.INIT -> {
                    dateStateFlow.value = (DateTimeUtils.getTodayDate())
                }
            }
        }
    }

    private fun weekAmountEvent(move: LogContract.Move) {
        launch {
            when(move) {
                LogContract.Move.LEFT -> {
                    val currentIndex = getCurrentWeekIndex()
                    if(currentIndex > 0) {
                        weekShareFlow.emit(
                            getCurrentDayOfWaterList()
                                .getWeekArray()[currentIndex - 1]
                                .first)
                    }
                }
                LogContract.Move.RIGHT -> {
                    val currentIndex = getCurrentWeekIndex()
                    if(currentIndex < getCurrentDayOfWaterList().getWeekArray().size - 1) {
                        weekShareFlow.emit(
                            getCurrentDayOfWaterList()
                                .getWeekArray()[currentIndex + 1]
                                .first)
                    }
                }
                LogContract.Move.INIT -> {
                    weekShareFlow.emit(DateTimeUtils.getTodayDate())
                }
            }
        }
    }

    private fun monthAmountEvent(move: LogContract.Move) {
        launch {
            when(move) {
                LogContract.Move.LEFT -> {
                    val currentIndex = getCurrentMonthIndex()
                    if(currentIndex > 0) {
                        monthShareFlow.emit(
                            getCurrentDayOfWaterList()
                                .getWeekArray()[currentIndex - 1]
                                .first)
                    }
                }
                LogContract.Move.RIGHT -> {
                    val currentIndex = getCurrentMonthIndex()
                    if(currentIndex < getCurrentDayOfWaterList().getMonthArray().size - 1) {
                        monthShareFlow.emit(
                            getCurrentDayOfWaterList()
                                .getWeekArray()[currentIndex + 1]
                                .first)
                    }
                }
                LogContract.Move.INIT -> {
                    monthShareFlow.emit(DateTimeUtils.getTodayDate())
                }
            }
        }
    }

    //현재 날짜로 조회한 DayOfWater
//    private suspend fun getDayAmount(date: String) {
//        val dayAmount = waterRepository.getAmount(date)
//        if(dayAmount != null) {
//            val list = DayOfWaterList(arrayListOf(dayAmount.toMap()))
//            setState { LogContract.State.Complete(list, LogContract.DateUnit.DAY) }
//        } else {
//            waterRepository.createAmount(date)
//        }
//    }

    //현재 날짜에 해당하는 주의 월요일 ~ 일요일까지의 DayOfWater 리스트
    private fun getWeekAmount(date: String) {
        val result = waterRepository.getAmountWeekBy(date)
        val weekAmount = DayOfWaterList(
            result.map {
                it.toMap()
            }
        )
        if(weekAmount.list.isNotEmpty()) {
            setState { LogContract.State.Complete(weekAmount, LogContract.DateUnit.WEEK) }
        } else {
            setState { LogContract.State.Error }
        }
    }

    //현재 날짜에 해당하는 월의 1일 ~ 30일(31일) 까지의 DayOfWater 리스트
    private fun getMonthAmount(date: String) {
        val result = waterRepository.getAmountMonthBy(date)
        val monthAmount = DayOfWaterList(
            result.map {
                it.toMap()
            }
        )
        if(monthAmount.list.isNotEmpty()) {
            setState { LogContract.State.Complete(monthAmount, LogContract.DateUnit.MONTH) }
        } else {
            setState { LogContract.State.Error }
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
            waterRepository.deleteAmount(dateStateFlow.value, water.dateTime)
        }
    }

    private fun getCurrentDayOfWaterList(): DayOfWaterList = allDayOfWaterLiveData.value

    //List<DayOfWater>에서 현재 세팅된 날짜랑 같은 마지막 DayOfWater의 인덱스
    private fun getCurrentDateWaterIndex(): Int {
        return getCurrentDayOfWaterList().list
            .indexOfLast { it.date == dateStateFlow.value }
    }

    private fun getCurrentWeekIndex(): Int {
        val date = weekLiveData.value ?: ""
        return getCurrentDayOfWaterList().getWeekArray().indexOfFirst {
            it.first <= date
                    && date <= it.second
        }
    }

    private fun getCurrentMonthIndex(): Int {
        val date = monthLiveData.value ?: ""
        return getCurrentDayOfWaterList().getMonthArray().indexOfFirst {
            it.first <= date
                    && date <= it.second
        }
    }

    //LogEditBottomDialog에서 사용할 메서드
    fun addAmount(amount: Int, date: String) {
        launch {
            waterRepository.addAmount(dateStateFlow.value, amount, date)
        }
    }

    fun updateAmount(origin: Water, amount: Int, date: String) {
        launch {
            waterRepository.updateAmount(dateStateFlow.value, origin, amount, date)
        }
    }
}