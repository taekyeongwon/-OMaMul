package com.tkw.alarm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.tkw.base.BaseViewModel
import com.tkw.base.launch
import com.tkw.domain.AlarmRepository
import com.tkw.domain.PrefDataRepository
import com.tkw.domain.model.AlarmEtcSettings
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmModeSetting
import com.tkw.domain.model.AlarmSettings
import com.tkw.domain.model.RingTone
import com.tkw.domain.model.RingToneMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

@HiltViewModel
class WaterAlarmViewModel @Inject constructor(
    private val prefDataRepository: PrefDataRepository,
    private val alarmRepository: AlarmRepository
): BaseViewModel() {

    private val isAlarmEnabled = prefDataRepository.fetchAlarmEnableFlag()
    suspend fun getNotificationEnabled() = isAlarmEnabled.first() ?: false

    suspend fun setNotificationEnabled(flag: Boolean) {
        prefDataRepository.saveAlarmEnableFlag(flag)
    }

    private val alarmSettingsFlow: Flow<AlarmSettings> = alarmRepository.getAlarmSetting()

    val periodModeSettingsLiveData: LiveData<AlarmModeSetting> =
        alarmRepository.getAlarmModeSetting(AlarmMode.PERIOD).asLiveData()

    val customModeSettingsLiveData: LiveData<AlarmModeSetting> =
        alarmRepository.getAlarmModeSetting(AlarmMode.CUSTOM).asLiveData()

    val alarmSettings: LiveData<AlarmSettings> =
        alarmSettingsFlow.asLiveData()

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

    private val _modifyMode = MutableLiveData(false)
    val modifyMode: LiveData<Boolean> = _modifyMode

    fun wakeAllAlarm() {
        launch {
            alarmRepository.wakeAllAlarm()
        }
    }

    fun cancelAllAlarm() {
        launch {
            alarmRepository.cancelAllAlarm()
        }
    }

    fun clearAlarm(mode: AlarmMode) {
        launch {
            alarmRepository.allDelete(mode)
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
            alarmRepository.update(newSetting)
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
            alarmRepository.update(newSetting)
        }
    }

    fun updateAlarmModeSetting(setting: AlarmModeSetting) {
        launch {
            alarmRepository.updateAlarmModeSetting(setting)
        }
    }

    fun setAlarm(alarmId: Int, startTime: Long, interval: Long) {
        launch {
            alarmRepository.setAlarm(alarmId, startTime, interval)
        }
    }

    fun deleteAlarm(alarmId: Int) {
        launch {
            alarmRepository.deleteAlarm(alarmId)
        }
    }

    fun setModifyMode(flag: Boolean) {
        _modifyMode.value = flag
    }
}