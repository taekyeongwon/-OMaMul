package com.tkw.omamul.ui.view.water.cup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.tkw.omamul.base.AppError
import com.tkw.omamul.base.BaseViewModel
import com.tkw.omamul.base.launch
import com.tkw.omamul.common.SingleLiveEvent
import com.tkw.omamul.data.CupRepository
import com.tkw.omamul.data.model.Cup
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

class CupViewModel(
    private val cupRepository: CupRepository,
    private val params: Cup
): BaseViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val cupListLiveData: LiveData<List<Cup>> =
        cupRepository.getCupList().mapLatest {
            it.toMap().cupList  //Flow<CupListEntity> -> Flow<List<Cup>>으로 최신값 매핑
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
        //createMode true면 추가 모드, 그 외 수정 모드
        _createMode.value = params.createMode
    }

    fun insertCup() {
        if(!validateCheck()) {
            _toastEvent.value = AppError(100)
            return
        }
        launch {
            val cupName = cupNameLiveData.value!!
            val cupAmount = cupAmountLiveData.value!!
            cupRepository.insertCup(cupName, cupAmount)
            _nextEvent.call()
        }
    }

    fun updateCup() {
        if(!validateCheck()) {
            _toastEvent.value = AppError(100)
            return
        }
        launch {
            val cupName = cupNameLiveData.value!!
            val cupAmount = cupAmountLiveData.value!!
            cupRepository.updateCup(params.cupId, cupName, cupAmount)
            _nextEvent.call()
        }
    }

    fun deleteCup(cupId: String) {
        launch {
            cupRepository.deleteCup(cupId)
        }
    }

    private fun validateCheck(): Boolean = cupNameLiveData.value!!.isNotBlank()

    fun updateAll(list: List<Cup>) {
        launch {
            runCatching {   //cancelException도 동일하게 onFailure타므로 주의
                cupRepository.updateAll(list)
            }.onSuccess {
                _nextEvent.call()
            }.onFailure {   //CoroutineExceptionHandler에서 잡히도록 throw
                throw it
            }
        }
    }
}