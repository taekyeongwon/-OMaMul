package com.tkw.cup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.tkw.base.AppError
import com.tkw.base.BaseViewModel
import com.tkw.base.launch
import com.tkw.common.SingleLiveEvent
import com.tkw.domain.CupRepository
import com.tkw.domain.model.Cup
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest

@HiltViewModel(assistedFactory = CupViewModel.AssistFactory::class)
class CupViewModel
@AssistedInject constructor(
    private val cupRepository: CupRepository,
    @Assisted private val params: Cup
): BaseViewModel() {

    @AssistedFactory
    interface AssistFactory {
        fun create(params: Cup): CupViewModel
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val cupListLiveData: LiveData<List<Cup>> =
        cupRepository.getCupList().mapLatest {
            it.cupList  //Flow<CupListEntity> -> Flow<List<Cup>>으로 최신값 매핑
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

    private val _modifyMode = MutableLiveData(false)
    val modifyMode: LiveData<Boolean> = _modifyMode

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

    fun setModifyMode(flag: Boolean) {
        _modifyMode.value = flag
    }
}