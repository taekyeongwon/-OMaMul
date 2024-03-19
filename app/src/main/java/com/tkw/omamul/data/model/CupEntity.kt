package com.tkw.omamul.data.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class CupEntity: RealmObject {
    @PrimaryKey
    var cupId: Int = 0
    var cupName: String = ""
    var cupAmount: Int = 0

    fun toMap(): Cup = Cup(cupId, cupName, cupAmount)
}