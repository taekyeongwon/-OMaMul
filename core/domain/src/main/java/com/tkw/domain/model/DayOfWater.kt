package com.tkw.domain.model

import java.time.LocalTime
import java.time.format.DateTimeFormatter

data class DayOfWater(
    val date: String,
    val dayOfList: List<Water>
) {
    fun getTotalIntakeByDate(): Int {
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
        return getLocalTime().hour
    }

    fun getMinuteFromDate(): Int {
        return getLocalTime().minute
    }

    private fun getLocalTime(): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalTime.parse(dateTime, formatter)
    }
}

data class DayOfWaterList(
    val list: List<DayOfWater>
) {
    fun getArray(transformer: DayTransformer): List<Pair<String, DayOfWaterList>> {
        return transformer.onTransform(list)
    }

    fun getTotalIntake() = list.sumOf { it.getTotalIntakeByDate() }

    fun getTotalAchieve(goal: Int) = list.filter { it.getTotalIntakeByDate() >= goal }.size
}

interface DayTransformer {
    //key값이 같은 날짜끼리 묶은 리스트 값 반환
    fun onTransform(list: List<DayOfWater>): List<Pair<String, DayOfWaterList>>
}