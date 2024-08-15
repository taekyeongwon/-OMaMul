package com.tkw.setting

import com.tkw.base.BaseViewModel
import com.tkw.domain.PrefDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel
@Inject constructor(
    private val prefDataRepository: PrefDataRepository
): BaseViewModel() {

}