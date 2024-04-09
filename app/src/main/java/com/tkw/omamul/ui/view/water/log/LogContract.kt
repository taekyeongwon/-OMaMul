package com.tkw.omamul.ui.view.water.log

import com.tkw.omamul.base.IEvent
import com.tkw.omamul.base.ISideEffect
import com.tkw.omamul.base.IState
import com.tkw.omamul.data.model.DayOfWaterList
import com.tkw.omamul.data.model.Water

class LogContract {
    sealed class Event: IEvent {
        class DayAmountEvent(val move: Move): Event()
        class WeekAmountEvent(val move: Move): Event()
        class MonthAmountEvent(val move: Move): Event()
        object ShowAddDialog: Event()
        class ShowEditDialog(val water: Water): Event()
        class RemoveDayAmount(val water: Water): Event()
    }

    sealed class SideEffect: ISideEffect {
        class ShowEditDialog(val water: Water?): SideEffect()
    }

    sealed class State: IState {
        data class Loading(val flag: Boolean): State()
        data class Complete(val data: DayOfWaterList, val unit: DateUnit): State()
        object Error: State()
    }

    enum class Move {
        LEFT, RIGHT, INIT
    }

    enum class DateUnit {
        DAY, WEEK, MONTH
    }
}