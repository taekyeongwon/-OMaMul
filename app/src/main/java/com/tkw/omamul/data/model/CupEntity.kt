package com.tkw.omamul.data.model

import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class CupEntity: EmbeddedRealmObject {
    var cupId: ObjectId = ObjectId.invoke()
    var cupName: String = ""
    var cupAmount: Int = 0

    fun toMap(): Cup = Cup(cupId.toHexString(), cupName, cupAmount)
}