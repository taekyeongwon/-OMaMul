package com.tkw.domain

import com.tkw.domain.model.AlarmSettings

interface AlarmRepository {
    fun update(setting: AlarmSettings)
}