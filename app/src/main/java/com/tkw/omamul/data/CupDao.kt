package com.tkw.omamul.data

import com.tkw.omamul.data.model.CupEntity
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface CupDao: RealmDao<CupEntity> {
    fun getCupListFlow(): Flow<ResultsChange<CupEntity>>
    suspend fun insertCup(obj: CupEntity)
    suspend fun updateCup(obj: CupEntity)
    suspend fun deleteCup(obj: CupEntity)
}