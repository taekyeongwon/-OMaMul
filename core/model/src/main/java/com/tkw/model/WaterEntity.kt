package com.tkw.model

import com.tkw.common.util.DateTimeUtils
import io.realm.kotlin.types.EmbeddedRealmObject

class WaterEntity: EmbeddedRealmObject {
    var dateTime: String = ""
    var amount: Int = 0

    fun toMap() = Water(
        dateTime = this@WaterEntity.dateTime,
        amount = this@WaterEntity.amount
    )

    fun getNanoOfDate(): Long {
        return DateTimeUtils.getTimeFromFullFormat(dateTime).toNanoOfDay()
    }
}