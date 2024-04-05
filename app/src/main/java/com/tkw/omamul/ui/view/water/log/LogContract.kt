package com.tkw.omamul.ui.view.water.log

import com.tkw.omamul.base.IEvent
import com.tkw.omamul.base.ISideEffect
import com.tkw.omamul.base.IState
import com.tkw.omamul.data.model.DayOfWater
import com.tkw.omamul.data.model.DayOfWaterList
import com.tkw.omamul.data.model.Water
import com.tkw.omamul.ui.view.init.InitContract

class LogContract {
    sealed class Event: IEvent {
        class GetDayAmount(val move: Move): Event()
        class GetWeekAmount(val move: Move): Event()
        class GetMonthAmount(val move: Move): Event()
        object AddDayAmount: Event()
        object EditDayAmount: Event()
        class RemoveDayAmount(val water: Water): Event()
    }

    sealed class SideEffect: ISideEffect {
        class ShowEditDialog(val isEdit: Boolean): SideEffect()
    }

    sealed class State: IState {
        data class Loading(val flag: Boolean): State()
        data class Complete(val list: DayOfWaterList): State()
        object Error: State()
    }

    enum class Move {
        LEFT, RIGHT, INIT
    }
}