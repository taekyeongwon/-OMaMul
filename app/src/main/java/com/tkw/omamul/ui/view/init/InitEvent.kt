package com.tkw.omamul.ui.view.init

sealed interface InitEvent {
    class SaveIntake(val amount: Int): InitEvent
}

sealed interface InitSideEffect {
    object CompleteIntake: InitSideEffect
}