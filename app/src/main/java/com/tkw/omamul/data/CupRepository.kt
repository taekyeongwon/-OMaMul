package com.tkw.omamul.data

import com.tkw.omamul.data.model.CupEntity
import com.tkw.omamul.data.model.CupEntityRequest
import com.tkw.omamul.data.model.CupListEntity
import kotlinx.coroutines.flow.Flow

interface CupRepository {
    fun getCupById(id: String): CupEntity?

    fun getCupList(): Flow<CupListEntity>

    suspend fun createList()

    suspend fun insertCup(obj: CupEntityRequest)

    suspend fun updateCup(cupId: String, target: CupEntityRequest)

    suspend fun updateAll(list: List<CupEntityRequest>)

    suspend fun deleteCup(cupId: String)
}