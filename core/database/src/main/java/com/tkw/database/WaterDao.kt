package com.tkw.database

import com.tkw.database.model.DayOfWaterEntity
import com.tkw.database.model.WaterEntity
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface WaterDao: RealmDao {
    fun getDayOfWater(date: String): DayOfWaterEntity?

    fun getAllDayOfWater(): Flow<ResultsChange<DayOfWaterEntity>>
    fun getWater(date: String, dateTime: String): WaterEntity?
    fun getAmountFlow(date: String): Flow<ResultsChange<DayOfWaterEntity>>
    suspend fun addAmount(date: String, newObj: WaterEntity)
    suspend fun removeAmount(selectedDate: String, dateTime: String)
    suspend fun updateAmount(selectedDate: String, origin: WaterEntity, target: WaterEntity)
}