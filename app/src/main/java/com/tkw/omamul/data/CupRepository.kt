package com.tkw.omamul.data

import com.tkw.omamul.data.model.CupEntity
import com.tkw.omamul.data.model.CupListEntity
import kotlinx.coroutines.flow.Flow

interface CupRepository {
    fun getCupById(id: String): CupEntity?

    fun getCupList(): Flow<CupListEntity>

    suspend fun createList()

    suspend fun insertCup(obj: CupEntity)

    suspend fun updateCup(origin: CupEntity, target: CupEntity)

    suspend fun updateAll(list: List<CupEntity>)

    suspend fun deleteCup(obj: CupEntity)
}