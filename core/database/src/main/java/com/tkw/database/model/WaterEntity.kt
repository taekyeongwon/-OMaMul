package com.tkw.database.model

import io.realm.kotlin.types.EmbeddedRealmObject
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class WaterEntity: EmbeddedRealmObject {
    var dateTime: String = ""
    var amount: Int = 0

    fun getNanoOfDate(): Long {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return LocalTime.parse(dateTime, formatter).toNanoOfDay()
    }
}