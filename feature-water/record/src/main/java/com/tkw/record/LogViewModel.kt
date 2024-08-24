package com.tkw.record

import androidx.lifecycle.viewModelScope
import com.tkw.base.IntentBaseViewModel
import com.tkw.base.launch
import com.tkw.common.util.DateTimeUtils
import com.tkw.domain.PrefDataRepository
import com.tkw.domain.SettingRepository
import com.tkw.domain.WaterRepository
import com.tkw.domain.model.DayOfWater
import com.tkw.domain.model.DayOfWaterList
import com.tkw.domain.model.Water
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LogViewModel
@Inject constructor(
    private val waterRepository: WaterRepository,
    private val settingRepository: SettingRepository,
    private val prefDataRepository: PrefDataRepository
): IntentBaseViewModel
<LogContract.Event, LogContract.State, LogContract.SideEffect>() {

    //목표 섭취량
    suspend fun getIntakeAmount() = settingRepository.getSetting().first().intake.toFloat()

    private val today = MutableStateFlow(DateTimeUtils.getTodayDate())
    val dateStateFlow = MutableStateFlow(today.value)
    val weekStateFlow = MutableStateFlow(today.value)
    val monthStateFlow = MutableStateFlow(today.value)

    //전체 DayOfWater 리스트
    @OptIn(ExperimentalCoroutinesApi::class)
    private val dayFlow: StateFlow<DayOfWaterList> =
        today.flatMapLatest {
            waterRepository.getFilteringDayOfWaterList(it).mapLatest { list ->
                DayOfWaterList(list)
            }
        }.stateIn(
            initialValue = DayOfWaterList(arrayListOf()),
            started = SharingStarted.Eagerly,
            scope = viewModelScope
        )

    //전체 리스트에서 주별 단위로 묶은 리스트 StateFlow
    @OptIn(ExperimentalCoroutinesApi::class)
    private val weekFlow: StateFlow<List<Pair<String, DayOfWaterList>>> =
        dayFlow.flatMapLatest {
            flow {
                emit(it.getArray(WeekLog()))
            }
        }.stateIn(
            initialValue = listOf(
                Pair(
                    DateTimeUtils.getWeekDates(today.value).first,
                    DayOfWaterList(arrayListOf())
                )
            ),
            started = SharingStarted.Eagerly,
            scope = viewModelScope
        )

    //전체 리스트에서 월별 단위로 묶은 리스트 StateFlow
    @OptIn(ExperimentalCoroutinesApi::class)
    private val monthFlow: StateFlow<List<Pair<String, DayOfWaterList>>> =
        dayFlow.flatMapLatest {
            flow {
                emit(it.getArray(MonthLog()))
            }
        }.stateIn(
            initialValue = listOf(
                Pair(
                    DateTimeUtils.getMonthDates(today.value).first,
                    DayOfWaterList(arrayListOf())
                )
            ),
            started = SharingStarted.Eagerly,
            scope = viewModelScope
        )

    init {
        launch {
            //일 탭 기록 추가/수정/삭제 발생 시 갱신
            dayFlow.collect {
                setEvent(LogContract.Event.DayAmountEvent(LogContract.Move.INIT))
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
            is LogContract.Event.DayAmountEventByDate -> dayAmountEventByDate(event.date)
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
                        val prev = dayFlow.value.list[currentIndex - 1]
                        getDayAmount(prev)
                    }
                }
                LogContract.Move.RIGHT -> {
                    val currentIndex = getCurrentDateWaterIndex()
                    if(currentIndex < dayFlow.value.list.size - 1) {
                        //현재 세팅된 날짜보다 앞의 날짜 값으로 세팅
                        val next = dayFlow.value.list[currentIndex + 1]
                        getDayAmount(next)
                    }
                }
                LogContract.Move.INIT -> {
                    val currentIndex = getCurrentDateWaterIndex()
                    val current = if(currentIndex != -1) {
                        dayFlow.value.list[currentIndex]
                    } else {
                        DayOfWater(
                            today.value,
                            arrayListOf()
                        )
                    }
                    getDayAmount(current)
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
                        val prev = weekFlow.value[currentIndex - 1]
                        getWeekAmount(prev)
                    }
                }
                LogContract.Move.RIGHT -> {
                    val currentIndex = getCurrentWeekIndex()
                    if(currentIndex < weekFlow.value.size - 1) {
                        val next = weekFlow.value[currentIndex + 1]
                        getWeekAmount(next)
                    }
                }
                LogContract.Move.INIT -> {
                    val currentIndex = getCurrentWeekIndex()
                    val current = if(currentIndex != -1) {
                        weekFlow.value[currentIndex]
                    } else {
                        Pair(
                            today.value,
                            DayOfWaterList(arrayListOf())
                        )
                    }
                    getWeekAmount(current)
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
                        val prev = monthFlow.value[currentIndex - 1]
                        getMonthAmount(prev)
                    }
                }
                LogContract.Move.RIGHT -> {
                    val currentIndex = getCurrentMonthIndex()
                    if(currentIndex < monthFlow.value.size - 1) {
                        val next = monthFlow.value[currentIndex + 1]
                        getMonthAmount(next)
                    }
                }
                LogContract.Move.INIT -> {
                    val currentIndex = getCurrentMonthIndex()
                    val current = if(currentIndex != -1) {
                        monthFlow.value[currentIndex]
                    } else {
                        Pair(
                            today.value,
                            DayOfWaterList(arrayListOf())
                        )
                    }
                    getMonthAmount(current)
                }
            }
        }
    }

    private fun dayAmountEventByDate(date: String) {
        launch {
            waterRepository.getAmountByFlow(date).collect {
                getDayAmount(it)
            }
        }
    }

    fun setToday() {
        today.value = DateTimeUtils.getTodayDate()
    }

    fun setDate(date: String) {
        today.value = date
    }

    fun getSelectedDate(): String = dateStateFlow.value

    //현재 선택된 일자로 업데이트
    private fun getDayAmount(water: DayOfWater) {
        dateStateFlow.value = water.date
        val result = DayOfWaterList(arrayListOf(water))
        setState { LogContract.State.Complete(result, LogContract.DateUnit.DAY) }
        setDate(water.date)
    }

    //현재 선택된 날짜의 주간 데이터로 업데이트
    private fun getWeekAmount(pair: Pair<String, DayOfWaterList>) {
        weekStateFlow.value = pair.first
        val result = pair.second
        setState { LogContract.State.Complete(result, LogContract.DateUnit.WEEK) }
    }

    //현재 선택된 날짜의 월간 데이터로 업데이트
    private fun getMonthAmount(pair: Pair<String, DayOfWaterList>) {
        monthStateFlow.value = pair.first
        val result = pair.second
        setState { LogContract.State.Complete(result, LogContract.DateUnit.MONTH) }
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

    //전체 데이터에서 현재 세팅된 날짜랑 같은 마지막 DayOfWater의 인덱스
    private fun getCurrentDateWaterIndex(): Int {
        return dayFlow.value.list
            .indexOfLast { it.date == dateStateFlow.value }
    }

    //주간 데이터 리스트에서 주간 시작일(yyyy-MM-dd)과 동일한 주의 인덱스
    private fun getCurrentWeekIndex(): Int {
        val date = weekStateFlow.value
        val startWeekDate = DateTimeUtils.getWeekDates(date).first
        return weekFlow.value.indexOfFirst {
            it.first == startWeekDate
        }
    }

    //월간 데이터 리스트에서 월간 시작일(yyyy-MM-dd)과 동일한 월의 인덱스
    private fun getCurrentMonthIndex(): Int {
        val date = monthStateFlow.value
        val startMonthDate = DateTimeUtils.getMonthDates(date).first
        return monthFlow.value.indexOfFirst {
            it.first == startMonthDate
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
    //end
}