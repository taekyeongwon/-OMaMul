package com.tkw.omamul.data

import com.tkw.omamul.data.model.CupEntity
import com.tkw.omamul.data.model.CupEntityRequest
import com.tkw.omamul.data.model.CupListEntity
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface CupDao: RealmDao<CupListEntity> {
    fun getCup(id: String): CupEntity?

    fun getCupListFlow(): Flow<ResultsChange<CupListEntity>>

    suspend fun createList()

    suspend fun insertCup(obj: CupEntityRequest)

    suspend fun updateCup(cupId: String, target: CupEntityRequest)

    suspend fun updateAll(list: List<CupEntityRequest>)

    suspend fun deleteCup(cupId: String)
}