package com.tkw.omamul.data.local

import com.tkw.omamul.data.WaterDao
import com.tkw.omamul.data.WaterRepository
import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class WaterRepositoryImpl(private val waterDao: WaterDao): WaterRepository {
    override suspend fun getDayEntity(date: String): DayOfWaterEntity? =
        waterDao.getDayOfWater(date)

    override fun getAllDayEntity(): Flow<List<DayOfWaterEntity>> {
        val allDayOfWater = waterDao.getAllDayOfWater()
        return flow {
            allDayOfWater.collect {
                emit(it.list)
            }
        }
    }

    override suspend fun getWater(date: String, time: String): WaterEntity? =
        waterDao.getWater(date, time)

    override fun getAmountByFlow(date: String): Flow<DayOfWaterEntity> {
        val amountFlow = waterDao.getAmountFlow(date)
        return flow {
            amountFlow.collect {
                val count = it.list.firstOrNull()
                if(count == null) {
                    createAmount(date)
                } else {
                    this.emit(count)
                }
            }
        }
    }

    override suspend fun createAmount(date: String) {
        waterDao.insert(DayOfWaterEntity().apply {
            this.date = date
        })
    }

    override suspend fun addAmount(date: String, newObj: WaterEntity) =
        waterDao.addAmount(date, newObj)

    override suspend fun deleteAmount(date: String, obj: WaterEntity) =
        waterDao.removeAmount(date, obj)

    override suspend fun updateAmount(origin: WaterEntity, target: WaterEntity)
    = waterDao.updateAmount(origin, target)
}