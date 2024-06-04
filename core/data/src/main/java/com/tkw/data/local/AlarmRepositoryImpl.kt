package com.tkw.data.local

import com.tkw.database.AlarmDao
import com.tkw.domain.AlarmRepository
import com.tkw.domain.model.AlarmSettings
import javax.inject.Inject

class AlarmRepositoryImpl @Inject constructor(private val alarmDao: AlarmDao): AlarmRepository {
    override fun update(setting: AlarmSettings) {
        TODO("Not yet implemented")
    }
}