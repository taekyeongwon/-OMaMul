package com.tkw.domain

import com.tkw.domain.model.Cup
import com.tkw.domain.model.CupList
import kotlinx.coroutines.flow.Flow

interface CupRepository {
    fun getCupById(id: String): Cup?

    fun getCupList(): Flow<CupList>

    suspend fun createList()

    suspend fun insertCup(cupName: String, cupAmount: Int)

    suspend fun updateCup(cupId: String, cupName: String, cupAmount: Int)

    suspend fun updateAll(list: List<Cup>)

    suspend fun deleteCup(cupId: String)
}