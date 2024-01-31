package com.tkw.omamul.data.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class CountEntity: RealmObject {
    @PrimaryKey
    var id: ObjectId = ObjectId.invoke()
    var count: Int = 0
}