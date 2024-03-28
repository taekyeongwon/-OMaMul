package com.tkw.omamul.data

import com.tkw.omamul.data.model.Cup
import com.tkw.omamul.data.model.CupEntity
import com.tkw.omamul.data.model.CupListEntity
import kotlinx.coroutines.flow.Flow

interface CupRepository {
    fun getCupById(id: String): CupEntity?

    fun getCupList(): Flow<CupListEntity>

    suspend fun createList()

    suspend fun insertCup(cupName: String, cupAmount: Int)

    suspend fun updateCup(cupId: String, cupName: String, cupAmount: Int)

    suspend fun updateAll(list: List<Cup>)

    suspend fun deleteCup(cupId: String)
}