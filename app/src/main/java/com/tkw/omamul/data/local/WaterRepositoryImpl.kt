package com.tkw.omamul.data.local

import com.tkw.omamul.data.WaterRepository
import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WaterRepositoryImpl(private val waterDao: WaterDao): WaterRepository {
    override suspend fun getCountById(): Int {
        return waterDao.query()
    }

    override fun getQueryByFlow(): Flow<DayOfWaterEntity> {
        val countFlow = waterDao.queryStream(DayOfWaterEntity::class)
        return flow {
            countFlow.collect {
                val count = it.list.firstOrNull()
                if(count == null) {
                    updateCount()
                } else {
                    this.emit(count)
                }
            }
        }
    }

    override suspend fun updateCount() {
        waterDao.addAsync()
    }

    override suspend fun deleteCount(obj: WaterEntity) {
        waterDao.removeAsync(obj)
    }
}