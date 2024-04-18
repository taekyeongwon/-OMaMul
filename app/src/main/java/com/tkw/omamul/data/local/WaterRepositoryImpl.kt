package com.tkw.omamul.data.local

import com.tkw.common.util.DateTimeUtils
import com.tkw.omamul.data.WaterDao
import com.tkw.omamul.data.WaterRepository
import com.tkw.model.DayOfWaterEntity
import com.tkw.model.Water
import com.tkw.model.WaterEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WaterRepositoryImpl(private val waterDao: WaterDao): WaterRepository {
    override fun getAmount(date: String): DayOfWaterEntity? {
        return waterDao.getDayOfWater(date)
    }

    override fun getAllDayEntity(): Flow<List<DayOfWaterEntity>> {
        val allDayOfWater = waterDao.getAllDayOfWater()
        return flow {
            allDayOfWater.collect {
                //마신 물이 있거나 오늘 날짜인 경우만 차트에 표시하기 위해 필터링
                val list = it.list.filter { entity ->
                    entity.dayOfList.isNotEmpty() ||
                            entity.date == DateTimeUtils.getTodayDate()
                }
                emit(list)
            }
        }
    }

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

    override fun getAmountWeekBy(date: String): List<DayOfWaterEntity> {
        val week = DateTimeUtils.getWeekDates(date)
        return waterDao.getAmountDuring(week.first, week.second)
    }

    override fun getAmountMonthBy(date: String): List<DayOfWaterEntity> {
        val month = DateTimeUtils.getMonthDates(date)
        return waterDao.getAmountDuring(month.first, month.second)
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
        waterDao.updateAmount(selectedDate, origin.toMapEntity(), entity)
    }
}