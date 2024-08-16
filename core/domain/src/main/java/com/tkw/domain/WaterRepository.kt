package com.tkw.domain

import com.tkw.domain.model.DayOfWater
import com.tkw.domain.model.DayOfWaterList
import com.tkw.domain.model.Water
import kotlinx.coroutines.flow.Flow

interface WaterRepository {

    fun getAmount(date: String): DayOfWater?

    fun getAllDay(): Flow<List<DayOfWater>>

    fun getFilteringDayOfWaterList(includeDate: String): Flow<List<DayOfWater>>

    fun getAmountByFlow(date: String): Flow<DayOfWater>

    suspend fun createAmount(date: String)

    suspend fun addAmount(selectedDate: String, amount: Int, dateTime: String)

    suspend fun deleteAmount(selectedDate: String, dateTime: String)

    suspend fun updateAmount(selectedDate: String, origin: Water, amount: Int, dateTime: String)
}