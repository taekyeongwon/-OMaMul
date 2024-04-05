package com.tkw.omamul.ui.view.water.log

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tkw.omamul.base.IntentBaseViewModel
import com.tkw.omamul.common.util.DateTimeUtils
import com.tkw.omamul.data.WaterRepository
import com.tkw.omamul.data.model.DayOfWater
import com.tkw.omamul.data.model.DayOfWaterList
import com.tkw.omamul.data.model.Water
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    private val currentDayOfWaterList: List<DayOfWater> = allDayOfWaterLiveData.value.list

    override fun createInitialState(): LogContract.State {
        return LogContract.State.Loading(false)
    }

    override fun handleEvent(event: LogContract.Event) {
        when(event) {
            is LogContract.Event.GetDayAmount -> getDayAmount(event.move)
            is LogContract.Event.GetWeekAmount -> getWeekAmount(event.move)
            is LogContract.Event.GetMonthAmount -> getMonthAmount(event.move)
            LogContract.Event.AddDayAmount -> addDayAmount()
            is LogContract.Event.EditDayAmount -> editDayAmount()
            is LogContract.Event.RemoveDayAmount -> removeDayAmount(event.water)
        }
    }

    private fun getDayAmount(move: LogContract.Move) {
        viewModelScope.launch {
            when(move) {
                LogContract.Move.LEFT -> {
                    val currentIndex = getCurrentDateWaterIndex()
                    if(currentIndex > 0) {
                        //현재 세팅된 날짜 이전의 날짜 값으로 세팅
                        dateStringFlow.value = currentDayOfWaterList[currentIndex - 1].date
                        setEvent(LogContract.Event.GetDayAmount(LogContract.Move.INIT))
                    }
                }
                LogContract.Move.RIGHT -> {
                    val currentIndex = getCurrentDateWaterIndex()
                    if(currentIndex < currentDayOfWaterList.size - 1) {
                        //현재 세팅된 날짜보다 앞의 날짜 값으로 세팅
                        dateStringFlow.value = currentDayOfWaterList[currentIndex - 1].date
                        setEvent(LogContract.Event.GetDayAmount(LogContract.Move.INIT))
                    }
                }
                LogContract.Move.INIT -> {
                    val dayOfWater = waterRepository.getAmount(dateStringFlow.value)?.toMap()
                    if(dayOfWater != null) {
                        val list = DayOfWaterList(arrayListOf(dayOfWater))
                        setState { LogContract.State.Complete(list) }
                    } else {
                        setState { LogContract.State.Error }
                    }
                }
            }
        }
    }

    private fun getWeekAmount(move: LogContract.Move) {

    }

    private fun getMonthAmount(move: LogContract.Move) {

    }

    private fun addDayAmount() {
        setSideEffect { LogContract.SideEffect.ShowEditDialog(false) }
    }

    private fun editDayAmount() {
        setSideEffect { LogContract.SideEffect.ShowEditDialog(true) }
    }

    private fun removeDayAmount(water: Water) {
        viewModelScope.launch {
            waterRepository.deleteAmount(dateStringFlow.value, water.dateTime)
        }
    }

    //List<DayOfWater>에서 현재 세팅된 날짜랑 같은 마지막 DayOfWater의 인덱스
    private fun getCurrentDateWaterIndex(): Int {
        return allDayOfWaterLiveData.value.list
            .indexOfLast { it.date == dateStringFlow.value }
    }
}