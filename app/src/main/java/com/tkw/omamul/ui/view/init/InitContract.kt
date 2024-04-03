package com.tkw.omamul.ui.view.init

import com.tkw.omamul.base.IEvent
import com.tkw.omamul.base.ISideEffect
import com.tkw.omamul.base.IState

class InitContract {

    sealed class Event: IEvent {
        data class SaveLanguage(val lang: String): Event()
        data class SaveTime(val time: String): Event()
        data class SaveIntake(val amount: Int): Event()
    }

    sealed class SideEffect: ISideEffect {
        object OnMoveNext: SideEffect()
    }

    data class State(
        val isLoading: Boolean = false,
        val error: String? = null
    ): IState
}