package com.tkw.omamul.ui.view.init

import androidx.lifecycle.viewModelScope
import com.tkw.omamul.base.IntentBaseViewModel
import com.tkw.omamul.data.WaterRepository
import kotlinx.coroutines.launch

class InitViewModel(
    private val waterRepository: WaterRepository
): IntentBaseViewModel
<InitContract.Event, InitContract.State, InitContract.SideEffect>() {

    override fun handleEvent(event: InitContract.Event) {
        when(event) {
            is InitContract.Event.SaveLanguage -> saveLanguage()
            is InitContract.Event.SaveTime -> saveTime(event.wakeTime, event.sleepTime)
            is InitContract.Event.SaveIntake -> saveIntake()
            is InitContract.Event.ClickWakeUpTimePicker -> clickTimePicker(true)
            is InitContract.Event.ClickSleepTimePicker -> clickTimePicker(false)
        }
    }

    private fun saveLanguage() {
        save {

        }
    }

    private fun saveTime(wakeTime: String, sleepTime: String) {
        save {

        }
    }

    private fun saveIntake() {
        save {

        }
    }

    private fun clickTimePicker(flag: Boolean) {
        setState { InitContract.State.InitTimePicker(flag) }
    }

    private fun save(block: suspend() -> Unit) {
        viewModelScope.launch {
            setState { InitContract.State.Loading(true) }
            block()
            setState { InitContract.State.Loading(false) }
            setSideEffect { InitContract.SideEffect.OnMoveNext }
        }
    }
}