package com.tkw.database.model

import io.realm.kotlin.types.EmbeddedRealmObject
import org.mongodb.kbson.ObjectId

class CupEntity: EmbeddedRealmObject {
    var cupId: String = ObjectId.invoke().toHexString()
    var cupName: String = ""
    var cupAmount: Int = 0

    companion object {
        const val DEFAULT_CUP_LIST_ID = "default_cup_list_id"
    }
}