package com.tkw.database

import com.tkw.database.model.SettingEntity
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface SettingDao: RealmDao<SettingEntity> {
    suspend fun createSetting()

    suspend fun saveIntake(amount: Int)

    suspend fun saveUnit(unit: Int)

    fun getSetting(): Flow<ResultsChange<SettingEntity>>
}