package com.tkw.omamul.data.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import java.util.stream.Collectors

class DayOfWaterEntity: RealmObject {
    @PrimaryKey
    var date: String = ""
    var dayOfList: RealmList<WaterEntity> = realmListOf()

    fun getTotalWaterAmount(): String {
        return dayOfList.sumOf { water ->
            water.amount
        }.toString()
    }

//    fun getSortedList() = dayOfList.sortedBy { it.getHourFromDate() }   //추가할 때 데이터 정렬해서 넣기? 꺼낼 때 정렬?

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