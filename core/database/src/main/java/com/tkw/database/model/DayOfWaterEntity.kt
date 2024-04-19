package com.tkw.database.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class DayOfWaterEntity: RealmObject {
    @PrimaryKey
    var date: String = ""
    var dayOfList: RealmList<WaterEntity> = realmListOf()

    //nano초로 정렬된 리스트 가져올 때 사용.
    fun getSortedList(): List<WaterEntity> =
        dayOfList.sortedBy { it.getNanoOfDate() }
}