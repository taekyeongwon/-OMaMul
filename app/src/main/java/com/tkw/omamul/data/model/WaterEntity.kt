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

    fun getHourFromDate(): Int {
        return DateTimeUtils.getTimeFromFullFormat(date).hour
    }

    fun getNanoOfDate(): Long {
        return DateTimeUtils.getTimeFromFullFormat(date).toNanoOfDay()
    }
}