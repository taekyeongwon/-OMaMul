package com.tkw.omamul.data.local

import com.tkw.omamul.data.WaterDao
import com.tkw.omamul.data.WaterRepository
import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WaterRepositoryImpl(private val waterDao: WaterDao): WaterRepository {
    private val testDate = "20240205"
    override suspend fun getCount(): DayOfWaterEntity? = waterDao.getCount()

    override fun getCountByFlow(): Flow<DayOfWaterEntity> {
        val countFlow = waterDao.getCountFlow()
        return flow {
            countFlow.collect {
                val count = it.list.firstOrNull()
                if(count == null) {
                    createCount()
                } else {
                    this.emit(count)
                }
            }
        }
    }

    override suspend fun createCount() {
        waterDao.insert(DayOfWaterEntity().apply {
            date = testDate
        })
    }

    override suspend fun addCount(newObj: WaterEntity) = waterDao.addCount(newObj)

    override suspend fun deleteCount(obj: WaterEntity) = waterDao.removeCount(obj)

    override suspend fun updateAmount(origin: WaterEntity, target: WaterEntity)
    = waterDao.updateAmount(origin, target)
}