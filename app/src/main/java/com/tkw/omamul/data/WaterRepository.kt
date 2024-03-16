package com.tkw.omamul.data

import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import kotlinx.coroutines.flow.Flow

interface WaterRepository {
    suspend fun getCount(): DayOfWaterEntity?
    fun getCountByFlow(): Flow<DayOfWaterEntity>

    suspend fun createCount()
    suspend fun addCount(newObj: WaterEntity)
    suspend fun deleteCount(obj: WaterEntity)
    suspend fun updateAmount(origin: WaterEntity, target: WaterEntity)
}