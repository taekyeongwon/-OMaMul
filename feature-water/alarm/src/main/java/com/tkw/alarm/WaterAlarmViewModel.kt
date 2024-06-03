package com.tkw.alarm

import com.tkw.base.BaseViewModel
import com.tkw.domain.AlarmRepository
import com.tkw.domain.PrefDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
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
}