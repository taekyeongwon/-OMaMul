package com.tkw.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.tkw.base.BaseViewModel
import com.tkw.base.launch
import com.tkw.common.SingleLiveEvent
import com.tkw.common.util.DateTimeUtils
import com.tkw.domain.AlarmRepository
import com.tkw.domain.CupRepository
import com.tkw.domain.PrefDataRepository
import com.tkw.domain.WaterRepository
import com.tkw.domain.model.Cup
import com.tkw.domain.model.DayOfWater
import com.tkw.domain.model.Water
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class WaterViewModel
@Inject constructor(
    private val waterRepository: WaterRepository,
    private val cupRepository: CupRepository,
    private val alarmRepository: AlarmRepository,
    private val prefDataRepository: PrefDataRepository,
    private val savedStateHandle: SavedStateHandle
): BaseViewModel() {

    //최초 진입 여부
    private val initFlag = prefDataRepository.fetchInitialFlag()
    suspend fun getInitFlag(): Boolean = initFlag.first() ?: false

    //알람 권한 허용 여부
    private val isAlarmEnabled = prefDataRepository.fetchAlarmEnableFlag()
    suspend fun getNotificationEnabled() = isAlarmEnabled.first() ?: false

    //현재 날짜
    private val dateStringFlow = MutableStateFlow(DateTimeUtils.getTodayDate())

    //현재 날짜로 조회한 DayOfWater, 마지막 데이터 제거하기 위해 관찰
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

    //섭취량 변경 완료 여부
    private val _amountSaveEvent = SingleLiveEvent<Unit>()
    val amountSaveEvent: LiveData<Unit> = _amountSaveEvent

    //date값 변경에 따라 flow에서 새로운 DayOfWater 객체 collect하기 위한 메서드
    fun setToday() {
        dateStringFlow.value = DateTimeUtils.getTodayDate()
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

    suspend fun getIntakeAmount(default: Int): Int =
        prefDataRepository.fetchIntakeAmount().first() ?: default

    fun saveIntakeAmount(amount: Int) {
        launch {
            prefDataRepository.saveIntakeAmount(amount)
            _amountSaveEvent.call()
        }
    }

    fun resetAlarm() {
        launch {
            alarmRepository.wakeAllAlarm()
        }
    }
}