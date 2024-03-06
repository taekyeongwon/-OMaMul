package com.tkw.omamul.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tkw.omamul.common.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

open class BaseViewModel: ViewModel() {
    private val _alertFlag = SingleLiveEvent<String>()
    val alertFlag: LiveData<String> get() = _alertFlag

    private val _progressFlag = SingleLiveEvent<Boolean>()
    val progressFlag: LiveData<Boolean> get() = _progressFlag

    protected fun showAlert(msg: String) {
        _alertFlag.value = msg
    }
}

fun ViewModel.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
): Job = viewModelScope.launch(context + CoroutineExceptionHandler { _, throwable ->
    throwable.printStackTrace()
}) {
    block()
}