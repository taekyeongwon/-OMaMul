package com.tkw.omamul.ui.view.init

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.tkw.omamul.base.BaseViewModel
import com.tkw.omamul.base.launch
import com.tkw.omamul.data.WaterRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn

class InitViewModel(
    private val waterRepository: WaterRepository
): BaseViewModel() {

    private val events: MutableSharedFlow<InitEvent> = MutableSharedFlow()
//    val events = _events.asSharedFlow()

    private val _sideEffect = Channel<InitSideEffect>()

//    val state: StateFlow<InitIntakeState> = events.receiveAsFlow()
//        .runningFold(InitIntakeState(), ::reduceState)
//        .stateIn(viewModelScope, SharingStarted.Eagerly, InitIntakeState())

    val sideEffect = _sideEffect.receiveAsFlow()

//    private fun reduceState(current: InitIntakeState, event: InitEvent): InitIntakeState {
//        return when(event) {
//            InitEvent.SaveIntake -> current.copy()
//        }
//    }

    init {
        subscribeEvents()
    }

    private fun subscribeEvents() {
        launch {
            events.collect { event ->
                when(event) {
                    is InitEvent.SaveIntake -> saveIntake(event.amount)
                }
            }
        }
    }

    fun setEvent(event: InitEvent) {
        launch {
            events.emit(event)
        }
    }

    private fun saveIntake(amount: Int) {
        launch {
//            waterRepository.saveIntake(amount)
            Log.d("test", "saveIntake $amount")
            _sideEffect.send(InitSideEffect.CompleteIntake)
        }
    }
}