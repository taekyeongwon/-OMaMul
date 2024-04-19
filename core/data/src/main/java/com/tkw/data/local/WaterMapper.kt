package com.tkw.data.local

import com.tkw.database.model.DayOfWaterEntity
import com.tkw.database.model.WaterEntity
import com.tkw.domain.model.DayOfWater
import com.tkw.domain.model.Water

object WaterMapper {
    fun waterToEntity(water: Water): WaterEntity {
        return WaterEntity().apply {
            dateTime = water.dateTime
            amount = water.amount
        }
    }

    fun waterToModel(entity: WaterEntity): Water {
        return Water(
            dateTime = entity.dateTime,
            amount = entity.amount
        )
    }

    fun dayOfWaterToModel(entity: DayOfWaterEntity): DayOfWater {
        return DayOfWater(
            date = entity.date,
            dayOfList = toWaterList(entity)
        )
    }

    private fun toWaterList(entity: DayOfWaterEntity): List<Water> {
        val newArrayList = ArrayList<Water>()
        val sortedList = entity.getSortedList()
        for(water in sortedList) {
            newArrayList.add(waterToModel(water))
        }
        return newArrayList
    }
}