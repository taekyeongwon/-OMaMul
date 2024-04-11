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
    fun getWeekArray(): Array<Pair<String, String>> {
        val set = LinkedHashSet<Pair<String, String>>()
        list.forEach {
            set.add(DateTimeUtils.getWeekDates(it.date))
        }
        return set.toArray(arrayOf())
    }
}