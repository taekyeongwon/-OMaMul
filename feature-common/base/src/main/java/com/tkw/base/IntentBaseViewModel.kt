package com.tkw.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class IntentBaseViewModel
<E: IEvent, S: IState, SE: ISideEffect>: ViewModel() {
    private val initialState : S by lazy { createInitialState() }
    abstract fun createInitialState() : S

    private val _event = MutableSharedFlow<E>()
    val event = _event.asSharedFlow()

    private val _state: MutableStateFlow<S> = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    private val _sideEffect = Channel<SE>()
    val sideEffect = _sideEffect.receiveAsFlow()

    init {
        subscribeEvents()
    }

    private fun subscribeEvents() {
        viewModelScope.launch {
            event.collect {
                handleEvent(it)
            }
        }
    }

    abstract fun handleEvent(event: E)

    fun setEvent(event: E) {
        val newEvent = event
        viewModelScope.launch { _event.emit(newEvent) }
    }

    protected fun setState(fold: S.() -> S) {
        val newState = state.value.fold()
        _state.value = newState
    }

    protected fun setSideEffect(builder: () -> SE) {
        val sideEffect = builder()
        viewModelScope.launch { _sideEffect.send(sideEffect) }
    }
}