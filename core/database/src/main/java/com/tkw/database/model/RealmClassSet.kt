package com.tkw.database.model

object RealmClassSet {
    val realmEntities =
        setOf(
            DayOfWaterEntity::class,
            WaterEntity::class,
            CupListEntity::class,
            CupEntity::class,
            AlarmSettingsEntity::class,
            RingToneModeEntity::class,
            AlarmModeSettingEntity::class,
            PeriodAlarmListEntity::class,
            CustomAlarmListEntity::class,
            AlarmEntity::class,
            AlarmEtcSettingsEntity::class,
            SettingEntity::class
        )
}