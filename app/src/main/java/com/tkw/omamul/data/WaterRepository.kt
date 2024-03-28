package com.tkw.omamul.data

import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.Water
import kotlinx.coroutines.flow.Flow

interface WaterRepository {

    fun getAllDayEntity(): Flow<List<DayOfWaterEntity>>

    fun getAmountByFlow(date: String): Flow<DayOfWaterEntity>

    suspend fun createAmount(date: String)

    suspend fun addAmount(selectedDate: String, amount: Int, dateTime: String)

    suspend fun deleteAmount(selectedDate: String, dateTime: String)

    suspend fun updateAmount(selectedDate: String, origin: Water, amount: Int, dateTime: String)
}