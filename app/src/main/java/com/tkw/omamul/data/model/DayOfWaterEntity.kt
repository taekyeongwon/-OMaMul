package com.tkw.omamul.data.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class DayOfWaterEntity: RealmObject {
    @PrimaryKey
    var date: String = ""
    var dayOfList: RealmList<WaterEntity> = realmListOf()

    fun toMap() = DayOfWater(
        date = this@DayOfWaterEntity.date,
        dayOfList = toWaterList()
    )

    private fun toWaterList(): List<Water> {
        val newArrayList = ArrayList<Water>()
        val sortedList = getSortedList()
        for(water in sortedList) {
            newArrayList.add(water.toMap())
        }
        return newArrayList
    }

    //nano초로 정렬된 리스트 가져올 때 사용.
    private fun getSortedList(): List<WaterEntity> =
        dayOfList.sortedBy { it.getNanoOfDate() }
}