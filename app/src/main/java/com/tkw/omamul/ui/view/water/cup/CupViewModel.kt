package com.tkw.omamul.ui.view.water.cup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.tkw.omamul.R
import com.tkw.omamul.base.AppError
import com.tkw.omamul.base.BaseViewModel
import com.tkw.omamul.base.launch
import com.tkw.omamul.common.SingleLiveEvent
import com.tkw.omamul.data.CupRepository
import com.tkw.omamul.data.model.Cup
import com.tkw.omamul.data.model.CupEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn

class CupViewModel(
    private val cupRepository: CupRepository,
    private val params: Cup
): BaseViewModel() {

    private val cupListFlow: StateFlow<List<CupEntity>> =
        cupRepository.getCupList().stateIn(
            initialValue = arrayListOf(),
            started = SharingStarted.WhileSubscribed(5000),
            scope = viewModelScope
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val cupListLiveData: LiveData<List<Cup>> =
        cupListFlow.mapLatest {
            it.map { cup ->
                cup.toMap()
            }
        }.asLiveData()

    //cup create fragment에서 관찰할 변수
    val cupNameLiveData = MutableLiveData(params.cupName)
    val cupAmountLiveData = MutableLiveData(params.cupAmount)
    val buttonName = MutableLiveData<String>()

    private val _nextEvent = SingleLiveEvent<Unit>()
    val nextEvent: LiveData<Unit> = _nextEvent

    private val _createMode = MutableLiveData(true)
    val createMode: LiveData<Boolean> = _createMode
    //end

    private val _toastEvent = SingleLiveEvent<AppError>()
    val toastEvent: LiveData<AppError> = _toastEvent

    init {
        //cupId가 default -1이면 추가 모드, 그 외 수정 모드
        _createMode.value = params.cupId == -1
    }

    fun insertCup() {
        if(!validateCheck()) {
            _toastEvent.value = AppError(100)
            return
        }
        launch {
            var newCupId = 0
            val currentList = cupListFlow.value
            if(currentList.isNotEmpty()) {
                newCupId = currentList.last().cupId.plus(1)
            }

            val target = CupEntity().apply {
                cupId = newCupId
                cupName = cupNameLiveData.value!!
                cupAmount = cupAmountLiveData.value!!
            }
            cupRepository.insertCup(target)
            _nextEvent.call()
        }
    }

    fun updateCup() {
        if(!validateCheck()) {
            _toastEvent.value = AppError(100)
            return
        }
        launch {
            val target = CupEntity().apply {
                cupId = params.cupId
                cupName = cupNameLiveData.value!!
                cupAmount = cupAmountLiveData.value!!
            }
            cupRepository.updateCup(target)
            _nextEvent.call()
        }
    }

    fun deleteCup(cupId: Int) {
        launch {
            val target = cupRepository.getCupById(cupId)
            if(target != null) {
                cupRepository.deleteCup(target)
                _nextEvent.call()
            }
        }
    }

    private fun validateCheck(): Boolean = cupNameLiveData.value!!.isNotBlank()
}