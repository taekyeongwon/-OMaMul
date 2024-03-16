package com.tkw.omamul.data

import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface WaterDao: RealmDao<DayOfWaterEntity> {
    suspend fun getCount(): DayOfWaterEntity?
    fun getCountFlow(): Flow<ResultsChange<DayOfWaterEntity>>
    suspend fun addCount(newObj: WaterEntity)
    suspend fun removeCount(obj: WaterEntity)
    suspend fun updateAmount(origin: WaterEntity, target: WaterEntity)
}