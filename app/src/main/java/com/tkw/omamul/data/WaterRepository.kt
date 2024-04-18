package com.tkw.omamul.data

import com.tkw.model.DayOfWaterEntity
import com.tkw.model.Water
import kotlinx.coroutines.flow.Flow

interface WaterRepository {

    fun getAmount(date: String): DayOfWaterEntity?

    fun getAllDayEntity(): Flow<List<DayOfWaterEntity>>

    fun getAmountByFlow(date: String): Flow<DayOfWaterEntity>

    fun getAmountWeekBy(date: String): List<DayOfWaterEntity>

    fun getAmountMonthBy(date: String): List<DayOfWaterEntity>

    suspend fun createAmount(date: String)

    suspend fun addAmount(selectedDate: String, amount: Int, dateTime: String)

    suspend fun deleteAmount(selectedDate: String, dateTime: String)

    suspend fun updateAmount(selectedDate: String, origin: Water, amount: Int, dateTime: String)
}