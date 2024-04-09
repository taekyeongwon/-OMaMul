package com.tkw.omamul.data

import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface WaterDao: RealmDao<DayOfWaterEntity> {
    fun getDayOfWater(date: String): DayOfWaterEntity?

    fun getAllDayOfWater(): Flow<ResultsChange<DayOfWaterEntity>>
    fun getWater(date: String, dateTime: String): WaterEntity?
    fun getAmountFlow(date: String): Flow<ResultsChange<DayOfWaterEntity>>
    fun getAmountWeekFlow(start: String, end: String): Flow<ResultsChange<DayOfWaterEntity>>
    fun getAmountMonthFlow(start: String, end: String): Flow<ResultsChange<DayOfWaterEntity>>
    suspend fun addAmount(date: String, newObj: WaterEntity)
    suspend fun removeAmount(selectedDate: String, dateTime: String)
    suspend fun updateAmount(selectedDate: String, origin: WaterEntity, target: WaterEntity)
}