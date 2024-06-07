package com.tkw.record

import com.tkw.domain.model.DayOfWater
import com.tkw.domain.model.DayOfWaterList
import com.tkw.domain.model.DayTransformer
import com.tkw.common.util.DateTimeUtils

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