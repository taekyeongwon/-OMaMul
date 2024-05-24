package com.tkw.init

import androidx.lifecycle.viewModelScope
import com.tkw.base.IntentBaseViewModel
import com.tkw.domain.InitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitViewModel
@Inject constructor(
    private val initRepository: InitRepository
): IntentBaseViewModel
<InitContract.Event, InitContract.State, InitContract.SideEffect>() {
    override fun createInitialState(): InitContract.State {
        return InitContract.State.Loading(false)
    }

    override fun handleEvent(event: InitContract.Event) {
        when(event) {
            is InitContract.Event.SaveLanguage -> saveLanguage(event.lang)
            is InitContract.Event.SaveTime -> saveTime(event.wakeTime, event.sleepTime)
            is InitContract.Event.SaveIntake -> saveIntake(event.amount)
            is InitContract.Event.SaveInitialFlag -> saveInitialFlag(event.flag)
            is InitContract.Event.ClickWakeUpTimePicker -> clickTimePicker(true)
            is InitContract.Event.ClickSleepTimePicker -> clickTimePicker(false)
        }
    }

    private fun saveLanguage(lang: String) {
        save(InitContract.SideEffect.OnMoveNext) {
            initRepository.saveLanguage(lang)
        }
    }

    private fun saveTime(wakeTime: String, sleepTime: String) {
        save(InitContract.SideEffect.OnMoveNext) {
            initRepository.saveAlarmTime(wakeTime, sleepTime)
        }
    }

    private fun saveIntake(amount: Int) {
        save(InitContract.State.Complete) {
            initRepository.saveIntakeAmount(amount)
        }
    }

    private fun saveInitialFlag(flag: Boolean) {
        save(InitContract.SideEffect.OnMoveNext) {
            initRepository.saveInitialFlag(flag)
        }
    }

    private fun clickTimePicker(flag: Boolean) {
        setSideEffect { InitContract.SideEffect.InitTimePicker(flag) }
    }

    private fun save(state: InitContract.State, block: suspend() -> Unit) {
        viewModelScope.launch {
            setState { InitContract.State.Loading(true) }
            block()
            setState { InitContract.State.Loading(false) }
            setState { state }
        }
    }

    private fun save(sideEffect: InitContract.SideEffect, block: suspend() -> Unit) {
        viewModelScope.launch {
            setState { InitContract.State.Loading(true) }
            block()
            setState { InitContract.State.Loading(false) }
            setSideEffect { sideEffect }
        }
    }
}