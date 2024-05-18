package com.tkw.init

import com.tkw.base.IEvent
import com.tkw.base.ISideEffect
import com.tkw.base.IState

class InitContract {

    sealed class Event: IEvent {
        data class SaveLanguage(val lang: String): Event()
        data class SaveTime(val wakeTime: String, val sleepTime: String): Event()
        data class SaveIntake(val amount: Int): Event()
        data class SaveInitialFlag(val flag: Boolean): Event()
        object ClickWakeUpTimePicker: Event()
        object ClickSleepTimePicker: Event()
    }

    sealed class SideEffect: ISideEffect {
        object OnMoveNext: SideEffect()
        data class InitTimePicker(val flag: Boolean): SideEffect()
    }

    sealed class State: IState {
        data class Loading(val flag: Boolean): State()
        object Complete: State()
        data class Error(val msg: String): State()
    }
}