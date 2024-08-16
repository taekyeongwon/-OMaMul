package com.tkw.data.local

import com.tkw.common.util.DateTimeUtils
import com.tkw.database.WaterDao
import com.tkw.database.model.DayOfWaterEntity
import com.tkw.database.model.WaterEntity
import com.tkw.domain.WaterRepository
import com.tkw.domain.model.DayOfWater
import com.tkw.domain.model.DayOfWaterList
import com.tkw.domain.model.Water
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject


class WaterRepositoryImpl @Inject constructor(private val waterDao: WaterDao): WaterRepository {
    override fun getAmount(date: String): DayOfWater? = waterDao.getDayOfWater(date)?.let {
        WaterMapper.dayOfWaterToModel(it)
    }

    override fun getAllDay(): Flow<List<DayOfWater>> {
        val allDayOfWater = waterDao.getAllDayOfWater()
        return flow {
            allDayOfWater.collect {
                val dayOfWater = it.list.toList()
                emit(
                    dayOfWater.map {
                        WaterMapper.dayOfWaterToModel(it)
                    }
                )
            }
        }
    }

    override fun getFilteringDayOfWaterList(includeDate: String): Flow<List<DayOfWater>> {
        val allDayOfWater = waterDao.getAllDayOfWater()
        return flow {
            allDayOfWater.collect {
                //마신 물이 있거나 없더라도 표시할 날짜를 차트에 표시하기 위해 필터링
                val list = it.list.filter { entity ->
                    entity.dayOfList.isNotEmpty() ||
                            entity.date == includeDate
                }
                val sortedList = list.sortedBy { it.date }
                emit(sortedList.map {
                    WaterMapper.dayOfWaterToModel(it)
                })
            }
        }
    }

    override fun getAmountByFlow(date: String): Flow<DayOfWater> {
        val amountFlow = waterDao.getAmountFlow(date)
        return flow {
            amountFlow.collect {
                val count = it.list.firstOrNull()
                if(count == null) {
                    createAmount(date)
                } else {
                    this.emit(WaterMapper.dayOfWaterToModel(count))
                }
            }
        }
    }

    override suspend fun createAmount(date: String) {
        waterDao.insert(DayOfWaterEntity().apply {
            this.date = date
        })
    }

    override suspend fun addAmount(selectedDate: String, amount: Int, dateTime: String) {
        val entity = WaterEntity().apply {
            this.amount = amount
            this.dateTime = dateTime
        }
        waterDao.addAmount(selectedDate, entity)
    }

    override suspend fun deleteAmount(selectedDate: String, dateTime: String) =
        waterDao.removeAmount(selectedDate, dateTime)

    override suspend fun updateAmount(selectedDate: String, origin: Water, amount: Int, dateTime: String) {
        val entity = WaterEntity().apply {
            this.amount = amount
            this.dateTime = dateTime
        }
        waterDao.updateAmount(selectedDate, WaterMapper.waterToEntity(origin), entity)
    }
}