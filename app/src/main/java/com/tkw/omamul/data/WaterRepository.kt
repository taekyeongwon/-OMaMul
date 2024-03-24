package com.tkw.omamul.data

import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import kotlinx.coroutines.flow.Flow

interface WaterRepository {
    suspend fun getDayEntity(date: String): DayOfWaterEntity?

    fun getAllDayEntity(): Flow<List<DayOfWaterEntity>>

    fun getAmountByFlow(date: String): Flow<DayOfWaterEntity>

    suspend fun getWater(date: String, time: String): WaterEntity?

    suspend fun createAmount(date: String)

    suspend fun addAmount(date: String, newObj: WaterEntity)

    suspend fun deleteAmount(date: String, obj: WaterEntity)

    suspend fun updateAmount(origin: WaterEntity, target: WaterEntity)
}