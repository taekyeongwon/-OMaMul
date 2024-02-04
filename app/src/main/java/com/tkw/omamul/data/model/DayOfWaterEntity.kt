package com.tkw.omamul.data.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class DayOfWaterEntity: RealmObject {
    @PrimaryKey
    var date: String = ""
    var dayOfList: RealmList<WaterEntity> = realmListOf()
}