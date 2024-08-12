package com.tkw.alarm

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.tkw.alarmnoti.NotificationManager
import com.tkw.base.BaseViewModel
import com.tkw.base.launch
import com.tkw.common.SingleLiveEvent
import com.tkw.common.util.DateTimeUtils
import com.tkw.common.util.DateTimeUtils.toEpochMilli
import com.tkw.domain.AlarmRepository
import com.tkw.domain.PrefDataRepository
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmEtcSettings
import com.tkw.domain.model.AlarmList
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmModeSetting
import com.tkw.domain.model.AlarmSettings
import com.tkw.domain.model.RingToneMode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import java.lang.StringBuilder
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class WaterAlarmViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val prefDataRepository: PrefDataRepository,
    private val alarmRepository: AlarmRepository
): BaseViewModel() {

    companion object {
        const val TIME_UNIT_SECONDS: Long = 1000
    }

    private val _nextEvent = SingleLiveEvent<Unit>()
    val nextEvent: LiveData<Unit> = _nextEvent

    //알람 권한 허용 여부
    private val isAlarmEnabled = prefDataRepository.fetchAlarmEnableFlag()
    suspend fun getNotificationEnabled() = isAlarmEnabled.first()
    suspend fun setAlarmEnabled(flag: Boolean) {
        prefDataRepository.saveAlarmEnableFlag(flag)
    }

    //설정에서 알람 허용 여부
    private val isNotificationEnabled = MutableStateFlow(false)
    fun setNotificationEnabled(flag: Boolean) {
        isNotificationEnabled.value = flag
    }

    //알람 및 설정에서 알람 허용했는지 여부
    @OptIn(ExperimentalCoroutinesApi::class)
    fun isNotificationAlarmEnabled() = isAlarmEnabled
        .combine(isNotificationEnabled) { isAlarm, isNoti ->
            isAlarm && isNoti
        }

    private val alarmSettingsFlow: Flow<AlarmSettings> = alarmRepository.getAlarmSetting()

    val periodModeSettingsLiveData: LiveData<AlarmModeSetting> =
        alarmRepository.getAlarmModeSetting().asLiveData()

    val alarmSettings: LiveData<AlarmSettings> =
        alarmSettingsFlow.asLiveData()

    val customAlarmList: LiveData<AlarmList> =
        alarmRepository.getAlarmList(AlarmMode.CUSTOM).asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    val alarmRingTone: LiveData<RingToneMode> =
        alarmSettingsFlow.mapLatest {
            it.ringToneMode
        }.asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    val alarmMode: LiveData<AlarmMode> =
        alarmSettingsFlow.mapLatest {
            it.alarmMode
        }.asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    val alarmEtcSetting: LiveData<AlarmEtcSettings> =
        alarmSettingsFlow.mapLatest {
            it.etcSetting
        }.asLiveData()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val isStopWhenReachedGoal: Flow<Boolean> =
        alarmSettingsFlow.flatMapLatest {
            flow {
                emit(it.etcSetting.stopReachedGoal)
            }
        }

    val isReachedGoal: LiveData<Boolean> = prefDataRepository.fetchReachedGoal()
        .combine(isStopWhenReachedGoal) { isReachedGoal, stopReachedFlag ->
            isReachedGoal && stopReachedFlag
        }.distinctUntilChanged().asLiveData()

    //period 모드 화면 변경사항 체크용
    private val _tmpPeriodMode: MutableLiveData<AlarmModeSetting> = MutableLiveData()
    val tmpPeriodMode: LiveData<AlarmModeSetting> = _tmpPeriodMode

    //알람 변경에 따라 remainTime 재요청
    @OptIn(ExperimentalCoroutinesApi::class)
    val timeTickerLiveData = isNotificationAlarmEnabled()
        .flatMapLatest { timeTickerFlow }
        .asLiveData()

    //남은 시간을 텍스트 포맷으로 변경
    @OptIn(ExperimentalCoroutinesApi::class)
    private val timeTickerFlow: Flow<String> = alarmRepository.getRemainAlarmTime().flatMapLatest {
        flow {
            var remainTime = it
            if(it == -1L) {
                emit(getCustomString(com.tkw.ui.R.string.alarm_detail_empty))
            } else if(!isNotificationAlarmEnabled().first()) {
                emit(getCustomString(com.tkw.ui.R.string.alarm_detail_switch_off))
            } else {
                while (remainTime > 0) {
                    val text = getRemainTimeString(remainTime)
                    if(text.isEmpty()) {
                        emit(getCustomString(com.tkw.ui.R.string.alarm_ringing_soon))
                    } else {
                        emit(
                            String.format(
                                getCustomString(com.tkw.ui.R.string.alarm_detail_remain),
                                text
                            )
                        )
                    }
                    remainTime -= TIME_UNIT_SECONDS
                    delay(TIME_UNIT_SECONDS)
                }
            }
        }
    }

    fun wakeAllAlarm() {
        launch {
            alarmRepository.wakeAllAlarm()
        }
    }

    suspend fun delayAllAlarm(isDelayed: Boolean, isNotificationEnabled: Boolean = true) {
        launch {
            alarmRepository.delayAllAlarm(isDelayed, isNotificationEnabled)
        }
    }

    fun sleepAllAlarm() {
        launch {
            alarmRepository.sleepAllAlarm(alarmSettingsFlow.first().alarmMode)
        }
    }

    suspend fun clearAlarm(mode: AlarmMode) {
        launch {
            alarmRepository.deleteAllAlarm(mode)
        }
    }

    fun updateRingToneMode(mode: RingToneMode) {
        launch {
            val currentSetting = alarmSettingsFlow.first()
            val newSetting = AlarmSettings(
                mode,
                currentSetting.alarmMode,
                currentSetting.etcSetting
            )
            alarmRepository.updateAlarmSetting(newSetting)
        }
    }

    fun updateAlarmMode(mode: AlarmMode) {
        launch {
            val currentSetting = alarmSettingsFlow.first()
            val newSetting = AlarmSettings(
                currentSetting.ringToneMode,
                mode,
                currentSetting.etcSetting
            )
            alarmRepository.sleepAllAlarm(currentSetting.alarmMode) //모드 변경 시 이전 모드의 알람 전부 알람매니저에서 해제
            alarmRepository.updateAlarmSetting(newSetting)
        }
    }

    suspend fun updateEtcSetting(etcSettings: AlarmEtcSettings) {
        launch {
            val currentSetting = alarmSettingsFlow.first()
            val newSetting = AlarmSettings(
                currentSetting.ringToneMode,
                currentSetting.alarmMode,
                etcSettings
            )
            alarmRepository.updateAlarmSetting(newSetting)
        }
    }

    suspend fun updateAlarmModeSetting(setting: AlarmModeSetting) {
        alarmRepository.updateAlarmModeSetting(setting)
    }

    suspend fun setPeriodAlarm(period: AlarmModeSetting) {
        if(period.selectedDate.isNotEmpty()) {
            var start = period.startTime.toEpochMilli()
            val end = period.endTime.toEpochMilli()
            val interval = period.interval * 1000
            val alarmList = ArrayList<Alarm>()

            while(start < end) {
                alarmList.add(
                    Alarm(
                        DateTimeUtils.getDateTimeInt(start),
                        start,
                        period.selectedDate,
                        true
                    )
                )
                start += interval
            }
            setAlarmList(alarmList)
        }
    }

    suspend fun setCustomAlarm(alarm: Alarm) {
        alarmRepository.setAlarm(alarm, isNotificationAlarmEnabled().first(), isReachedGoal.value ?: false)
    }

    private suspend fun setAlarmList(list: List<Alarm>) {
        alarmRepository.setAlarmList(list, isNotificationAlarmEnabled().first(), isReachedGoal.value ?: false)
    }

    fun deleteAlarm(list: List<Alarm>) {
        launch {
            alarmRepository.deleteAlarm(list, AlarmMode.CUSTOM)
        }
    }

    fun updateList(list: List<Alarm>) {
        launch {
            alarmRepository.updateList(list, AlarmMode.CUSTOM)
        }
    }

    fun setTmpPeriodMode(period: AlarmModeSetting) {
        _tmpPeriodMode.value = period
    }

    fun saveReachedGoal(isReached: Boolean) {
        launch {
            prefDataRepository.saveReachedGoal(isReached)
        }
    }

    private fun getRemainTimeString(remainTime: Long): String {
        val text = StringBuilder()

        val days = remainTime / (1000 * 60 * 60 * 24)
        val hour = (remainTime / (1000 * 60 * 60)) % 24
        val minute = (remainTime / (1000 * 60)) % 60

        if (days != 0L) {
            text.append(days)
                .append(getCustomString(com.tkw.ui.R.string.day))
                .append(" ")
        }
        if (hour != 0L) {
            text.append(hour)
                .append(getCustomString(com.tkw.ui.R.string.hour))
                .append(" ")
        }
        if (minute != 0L) {
            text.append(minute)
                .append(getCustomString(com.tkw.ui.R.string.minute))
                .append(" ")
        }

        return text.toString()
    }

    private fun getCustomString(id: Int): String {
        return context.getString(id)
    }
}