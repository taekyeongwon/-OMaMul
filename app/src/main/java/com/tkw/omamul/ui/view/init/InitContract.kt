package com.tkw.omamul.ui.view.init

import com.tkw.omamul.base.IEvent
import com.tkw.omamul.base.ISideEffect
import com.tkw.omamul.base.IState

class InitContract {

    sealed class Event: IEvent {
        data class SaveLanguage(val lang: String): Event()
        data class SaveTime(val wakeTime: String, val sleepTime: String): Event()
        data class SaveIntake(val amount: Int): Event()
        object ClickWakeUpTimePicker: Event()
        object ClickSleepTimePicker: Event()
    }

    sealed class SideEffect: ISideEffect {
        object OnMoveNext: SideEffect()
    }

    sealed class State: IState {
        data class Loading(val flag: Boolean): State()
        object Complete: State()
        data class Error(val msg: String): State()
        data class InitTimePicker(val flag: Boolean): State()
    }
}