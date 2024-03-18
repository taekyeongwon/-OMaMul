package com.tkw.omamul.data.model

import com.tkw.omamul.common.util.DateTimeUtils
import io.realm.kotlin.ext.toRealmList

data class DayOfWater(
    var date: String,
    var dayOfList: List<Water>
) {
    fun getTotalWaterAmount(): String {
        return dayOfList.sumOf { water ->
            water.amount
        }.toString()
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
    var date: String = "",
    var amount: Int = 0
) {

    fun getHourFromDate(): Int {
        return DateTimeUtils.getTimeFromFullFormat(date).hour
    }
}