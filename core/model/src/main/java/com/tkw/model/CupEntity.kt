package com.tkw.model

import io.realm.kotlin.types.EmbeddedRealmObject
import org.mongodb.kbson.ObjectId

class CupEntity: EmbeddedRealmObject {
    var cupId: String = ObjectId.invoke().toHexString()
    var cupName: String = ""
    var cupAmount: Int = 0

    fun toMap(): Cup = Cup(cupId, cupName, cupAmount)
}