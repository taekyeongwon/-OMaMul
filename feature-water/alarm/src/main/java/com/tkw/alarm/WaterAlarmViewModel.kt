package com.tkw.alarm

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
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


    //현재 선택된 모드에서 가장 남은시간이 가까운 알람 가져오기
    @OptIn(ExperimentalCoroutinesApi::class)
    private val remainAlarmTime: Flow<Long> = alarmMode.asFlow()
        .flatMapLatest {
            alarmRepository.getAlarmList(it)
        }
        .flatMapLatest { result ->
            flow {
                if(result.alarmList.none { it.enabled }) {
                    emit(-1L)
                } else {
                    val closestAlarm = result.alarmList
                        .filter { it.enabled }
                        .minOf {
                            it.startTime - System.currentTimeMillis()
                        }
                    emit(closestAlarm)
                }
            }
        }

    //custom 모드 리스트 수정모드 관찰 변수
    private val _modifyMode = MutableLiveData(false)
    val modifyMode: LiveData<Boolean> = _modifyMode

    //남은 시간을 텍스트 포맷으로 변경
    @OptIn(ExperimentalCoroutinesApi::class)
    val timeTickerFlow: LiveData<String> = remainAlarmTime.flatMapLatest {
        flow {
            var remainTime = it
            if(it == -1L) {
                emit(getCustomString(com.tkw.ui.R.string.alarm_detail_empty))
            }
            while(remainTime > 0) {
                val text = StringBuilder()

                val days = remainTime / (1000 * 60 * 60 * 24)
                val hour = (remainTime / (1000 * 60 * 60)) % 24
                val minute = (remainTime / (1000 * 60)) % 60
                val second = (remainTime / 1000) % 60

                if(days != 0L) {
                    text.append(days)
                        .append(getCustomString(com.tkw.ui.R.string.day))
                        .append(" ")
                }
                if(hour != 0L) {
                    text.append(hour)
                        .append(getCustomString(com.tkw.ui.R.string.hour))
                        .append(" ")
                }
                if(minute != 0L) {
                    text.append(minute)
                        .append(getCustomString(com.tkw.ui.R.string.minute))
                        .append(" ")
                }
                text.append(second)
                    .append(getCustomString(com.tkw.ui.R.string.second))
                    .append(getCustomString(com.tkw.ui.R.string.remaining))

                emit(text.toString())
                remainTime -= TIME_UNIT_SECONDS
                delay(TIME_UNIT_SECONDS)
            }
        }
    }.asLiveData()

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
        alarmRepository.setAlarm(alarm)
    }

    private suspend fun setAlarmList(list: List<Alarm>) {
        alarmRepository.setAlarmList(list)
    }

    fun deleteAlarm(alarmId: String) {
        launch {
            alarmRepository.deleteAlarm(alarmId, AlarmMode.CUSTOM)
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

    private fun getCustomString(id: Int): String {
        return context.getString(id)
    }
}