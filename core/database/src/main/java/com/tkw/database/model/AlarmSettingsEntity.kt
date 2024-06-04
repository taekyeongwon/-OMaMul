package com.tkw.database.model

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class AlarmSettingsEntity: RealmObject {
    @PrimaryKey
    var id: Int = 0
    private var ringToneMode: String = RingTone.BELL.state
    var ringToneEnum: RingTone
        get() = RingTone.valueOf(ringToneMode)
        set(value) {
            ringToneMode = value.state
        }
    var alarmStartTime: String = ""
    var alarmEndTime: String = ""
    var alarmMode: AlarmMode? = null
    var etcSetting: AlarmEtcSettings? = null
}

enum class RingTone(var state: String) {
    BELL("BELL"), VIBE("VIBE"), ALL("ALL"), IGNORE("IGNORE")
}

open class AlarmMode: EmbeddedRealmObject {
    var selectedDate: RealmList<Int> = realmListOf()
    var interval: Long = 0L
}

class Period: AlarmMode()

class Custom: AlarmMode()

class AlarmEtcSettings: EmbeddedRealmObject {
    var stopReachedGoal: Boolean = false
    var delayTomorrow: Boolean = false
}