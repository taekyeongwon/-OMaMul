package com.tkw.omamul.data

import com.tkw.model.CupEntity
import com.tkw.model.CupListEntity
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface CupDao: RealmDao<CupListEntity> {
    fun getCup(id: String): CupEntity?

    fun getCupListFlow(): Flow<ResultsChange<CupListEntity>>

    suspend fun createList()

    suspend fun insertCup(obj: CupEntity)

    suspend fun updateCup(target: CupEntity)

    suspend fun updateAll(list: List<CupEntity>)

    suspend fun deleteCup(cupId: String)
}