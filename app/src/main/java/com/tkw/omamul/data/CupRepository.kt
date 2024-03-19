package com.tkw.omamul.data

import com.tkw.omamul.data.model.CupEntity
import kotlinx.coroutines.flow.Flow

interface CupRepository {
    fun getCupList(): Flow<List<CupEntity>>
    suspend fun insertCup(obj: CupEntity)
    suspend fun updateCup(obj: CupEntity)
    suspend fun deleteCup(obj: CupEntity)
}