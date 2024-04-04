package com.tkw.omamul.ui.view.water.log

import com.tkw.omamul.base.IntentBaseViewModel
import com.tkw.omamul.data.WaterRepository

class LogViewModel(
    private val waterRepository: WaterRepository
): IntentBaseViewModel
<LogContract.Event, LogContract.State, LogContract.SideEffect>() {
    override fun createInitialState(): LogContract.State {
        return LogContract.State.Loading(false)
    }

    override fun handleEvent(event: LogContract.Event) {

    }
}