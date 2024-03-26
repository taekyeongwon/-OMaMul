package com.tkw.omamul.data.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class CupListEntity: RealmObject {
    @PrimaryKey
    var cupId: String = "default"
    var cupList: RealmList<CupEntity> = realmListOf()

    fun toMap() = CupList(
        cupId = cupId,
        cupList = cupList.map { it.toMap() }
    )
}