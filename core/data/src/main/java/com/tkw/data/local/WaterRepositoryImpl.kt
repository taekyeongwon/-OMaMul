package com.tkw.data.local

import com.tkw.database.WaterDao
import com.tkw.database.model.DayOfWaterEntity
import com.tkw.database.model.WaterEntity
import com.tkw.domain.WaterRepository
import com.tkw.domain.model.DayOfWater
import com.tkw.domain.model.Water
import com.tkw.domain.util.DateTimeUtils
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class WaterRepositoryImpl @Inject constructor(private val waterDao: WaterDao): WaterRepository {
    override fun getAmount(date: String): DayOfWater? = waterDao.getDayOfWater(date)?.let {
        WaterMapper.dayOfWaterToModel(it)
    }

    override fun getAllDayEntity(): Flow<List<DayOfWater>> {
        val allDayOfWater = waterDao.getAllDayOfWater()
        return flow {
            allDayOfWater.collect {
                //마신 물이 있거나 오늘 날짜인 경우만 차트에 표시하기 위해 필터링
                val list = it.list.filter { entity ->
                    entity.dayOfList.isNotEmpty() ||
                            entity.date == DateTimeUtils.getTodayDate()
                }
                emit(list.map {
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

    override fun getAmountWeekBy(date: String): List<DayOfWater> {
        val week = DateTimeUtils.getWeekDates(date)
        return waterDao.getAmountDuring(week.first, week.second).map {
            WaterMapper.dayOfWaterToModel(it)
        }
    }

    override fun getAmountMonthBy(date: String): List<DayOfWater> {
        val month = DateTimeUtils.getMonthDates(date)
        return waterDao.getAmountDuring(month.first, month.second).map {
            WaterMapper.dayOfWaterToModel(it)
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