package com.tkw.omamul.data.model

import com.tkw.omamul.common.util.DateTimeUtils

data class DayOfWater(
    val date: String,
    val dayOfList: List<Water>
) {
    fun getTotalWaterAmount(): Int {
        return dayOfList.sumOf { water ->
            water.amount
        }
    }

    //hour값을 key로 해당 시간대의 누적 합 계산
    fun getAccumulatedAmount(): Map<Int, Int> {
        val sortedMap = dayOfList.groupBy { it.getHourFromDate() }
        val resultMap = linkedMapOf<Int, Int>()
        var acc = 0
        for((k, v) in sortedMap) {
            acc += v.sumOf { it.amount }
            resultMap[k] = acc
        }
        return resultMap
    }
}

data class Water(
    val dateTime: String = "",
    val amount: Int = 0
) {
    fun getHourFromDate(): Int {
        return DateTimeUtils.getTimeFromFullFormat(dateTime).hour
    }

    fun toMapEntity() = WaterEntity().apply {
        dateTime = this@Water.dateTime
        amount = this@Water.amount
    }
}

data class DayOfWaterList(
    val list: List<DayOfWater>
) {
    fun getArray(transformer: DayTransformer): List<Pair<String, DayOfWaterList>> {
        return transformer.onTransform(list)
    }
}

interface DayTransformer {
    //key값이 같은 날짜끼리 묶은 리스트 값 반환
    fun onTransform(list: List<DayOfWater>): List<Pair<String, DayOfWaterList>>
}

class WeekLog: DayTransformer {
    override fun onTransform(list: List<DayOfWater>): List<Pair<String, DayOfWaterList>> {
        val map = LinkedHashMap<String, DayOfWaterList>()
        val sortedMap = list.groupBy {
            DateTimeUtils.getWeekDates(it.date).first
        }
        for((k, v) in sortedMap) {
            map[k] = DayOfWaterList(v)
        }

        return map.toList()
    }
}

class MonthLog: DayTransformer {
    override fun onTransform(list: List<DayOfWater>): List<Pair<String, DayOfWaterList>> {
        val map = LinkedHashMap<String, DayOfWaterList>()
        val sortedMap = list.groupBy {
            DateTimeUtils.getMonthDates(it.date).first
        }
        for((k, v) in sortedMap) {
            map[k] = DayOfWaterList(v)
        }

        return map.toList()
    }
}