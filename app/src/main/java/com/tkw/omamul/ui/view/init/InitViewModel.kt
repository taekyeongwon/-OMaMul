package com.tkw.omamul.ui.view.init

import androidx.lifecycle.viewModelScope
import com.tkw.omamul.base.IntentBaseViewModel
import com.tkw.omamul.data.WaterRepository
import kotlinx.coroutines.launch

class InitViewModel(
    private val waterRepository: WaterRepository
): IntentBaseViewModel
<InitContract.Event, InitContract.State, InitContract.SideEffect>() {

    override fun handleEvent(event: InitContract.Event) {
        when(event) {
            is InitContract.Event.SaveLanguage -> {}
            is InitContract.Event.SaveTime -> {}
            is InitContract.Event.SaveIntake -> saveIntake()
        }
    }

    private fun saveIntake() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            //저장
            setState { copy(isLoading = false) }
            setSideEffect { InitContract.SideEffect.OnMoveNext }
        }
    }
}