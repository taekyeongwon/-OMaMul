package com.tkw.omamul.data.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class CountEntity: RealmObject {
    @PrimaryKey
    var id: Int = 0
    var count: Int = 0
}