package com.tkw.omamul.data

import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface WaterDao: RealmDao<DayOfWaterEntity> {
    suspend fun getDayOfWater(date: String): DayOfWaterEntity?
    suspend fun getWater(date: String, time: String): WaterEntity?
    fun getAmountFlow(date: String): Flow<ResultsChange<DayOfWaterEntity>>
    suspend fun addAmount(date: String, newObj: WaterEntity)
    suspend fun removeAmount(date: String, obj: WaterEntity)
    suspend fun updateAmount(origin: WaterEntity, target: WaterEntity)
}