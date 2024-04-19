package com.tkw.database.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class CupListEntity: RealmObject {
    @PrimaryKey
    var cupId: String = CupEntity.DEFAULT_CUP_LIST_ID
    var cupList: RealmList<CupEntity> = realmListOf()
}