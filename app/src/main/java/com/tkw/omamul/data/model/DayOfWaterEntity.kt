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

    @Ignore
    //nano초로 정렬된 리스트 가져올 때 사용.
    var sortedList: () -> List<WaterEntity> = {
        dayOfList.sortedBy { it.getNanoOfDate() }
    }

    fun getTotalWaterAmount(): String {
        return dayOfList.sumOf { water ->
            water.amount
        }.toString()
    }

    //date값을 key로 해당 시간대의 누적 합 계산
    fun getAccumulatedAmount(): Map<Int, Int> {
        val sortedMap = sortedList().groupBy { it.getHourFromDate() }
        val resultMap = linkedMapOf<Int, Int>()
        var acc = 0
        for((k, v) in sortedMap) {
            acc += v.sumOf { it.amount }
            resultMap[k] = acc
        }
        return resultMap
    }
}