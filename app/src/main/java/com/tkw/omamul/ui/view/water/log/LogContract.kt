package com.tkw.omamul.ui.view.water.log

import com.tkw.omamul.base.IEvent
import com.tkw.omamul.base.ISideEffect
import com.tkw.omamul.base.IState
import com.tkw.omamul.ui.view.init.InitContract

class LogContract {
    sealed class Event: IEvent {

    }

    sealed class SideEffect: ISideEffect {

    }

    sealed class State: IState {
        data class Loading(val flag: Boolean): State()
    }
}