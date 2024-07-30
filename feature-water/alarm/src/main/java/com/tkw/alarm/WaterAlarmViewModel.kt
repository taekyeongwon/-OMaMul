package com.tkw.alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class WaterAlarmViewModel @Inject constructor(
    private val prefDataRepository: PrefDataRepository,
    private val alarmRepository: AlarmRepository
): BaseViewModel() {

    private val _nextEvent = SingleLiveEvent<Unit>()
    val nextEvent: LiveData<Unit> = _nextEvent

    private val isAlarmEnabled = prefDataRepository.fetchAlarmEnableFlag()
    suspend fun getNotificationEnabled() = isAlarmEnabled.first() ?: false

    suspend fun setNotificationEnabled(flag: Boolean) {
        prefDataRepository.saveAlarmEnableFlag(flag)
    }

    val prefSavedAlarmTime = prefDataRepository.fetchAlarmWakeTime()
        .combine(prefDataRepository.fetchAlarmSleepTime()) { wake, sleep ->
            if(wake != null && sleep != null) {
                val wakeTime = DateTimeUtils.getTimeFromFormat(wake)
                val sleepTime = DateTimeUtils.getTimeFromFormat(sleep)
                Pair(wakeTime, sleepTime)
            } else null
    }.asLiveData()

    suspend fun setAlarmTime(start: String, end: String) {
        prefDataRepository.saveAlarmTime(start, end)
    }

    private val alarmSettingsFlow: Flow<AlarmSettings> = alarmRepository.getAlarmSetting()

    val periodModeSettingsLiveData: LiveData<AlarmModeSetting> =
        alarmRepository.getAlarmModeSetting(AlarmMode.PERIOD).asLiveData()

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

    //period 모드 화면 변경사항 체크용
    private val _tmpPeriodMode: MutableLiveData<AlarmModeSetting> = MutableLiveData()
    val tmpPeriodMode: LiveData<AlarmModeSetting> = _tmpPeriodMode

    //custom 모드 리스트 수정모드 관찰 변수
    private val _modifyMode = MutableLiveData(false)
    val modifyMode: LiveData<Boolean> = _modifyMode

    fun wakeAllAlarm() {
        launch {
            alarmRepository.wakeAllAlarm()
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
            alarmRepository.sleepAllAlarm(mode)
            alarmRepository.updateAlarmSetting(newSetting)
        }
    }

    suspend fun updateAlarmModeSetting(setting: AlarmModeSetting) {
        alarmRepository.updateAlarmModeSetting(setting)
    }

    suspend fun setPeriodAlarm(period: AlarmModeSetting) {
        if(period.selectedDate.isNotEmpty()) {
            var start = prefSavedAlarmTime.value?.first?.toEpochMilli() ?: 0
            val end = prefSavedAlarmTime.value?.second?.toEpochMilli() ?: 0
            val interval = period.interval * 1000
            val alarmList = ArrayList<Alarm>()

            while(start < end) {
                alarmList.add(
                    Alarm(
                        DateTimeUtils.getTimeHHmm(start),
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
        alarmRepository.setAlarm(alarm)
    }

    private suspend fun setAlarmList(list: List<Alarm>) {
        alarmRepository.setAlarmList(list)
    }

    fun deleteAlarm(alarmId: Int) {
        launch {
            alarmRepository.deleteAlarm(alarmId, AlarmMode.CUSTOM)
            _nextEvent.call()
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

    fun setModifyMode(flag: Boolean) {
        _modifyMode.value = flag
    }
}