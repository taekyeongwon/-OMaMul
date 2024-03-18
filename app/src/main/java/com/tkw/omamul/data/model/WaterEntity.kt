package com.tkw.omamul.data.model

import com.tkw.omamul.common.util.DateTimeUtils
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class WaterEntity: EmbeddedRealmObject {
    var date: String = ""
    var amount: Int = 0

    fun toMap() = Water().apply {
        date = this@WaterEntity.date
        amount = this@WaterEntity.amount
    }

    fun getNanoOfDate(): Long {
        return DateTimeUtils.getTimeFromFullFormat(date).toNanoOfDay()
    }
}