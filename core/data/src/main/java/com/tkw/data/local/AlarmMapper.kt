package com.tkw.data.local

import com.tkw.database.model.AlarmEntity
import com.tkw.database.model.AlarmEtcSettingsEntity
import com.tkw.database.model.AlarmModeSettingEntity
import com.tkw.database.model.AlarmModeEntity
import com.tkw.database.model.AlarmSettingsEntity
import com.tkw.database.model.CustomEntity
import com.tkw.database.model.PeriodEntity
import com.tkw.database.model.RingToneEntity
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmEtcSettings
import com.tkw.domain.model.AlarmModeSetting
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmSettings
import com.tkw.domain.model.RingTone
import io.realm.kotlin.ext.toRealmList
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object AlarmMapper {

    fun alarmSettingToEntity(
        alarmSettings: AlarmSettings
    ): AlarmSettingsEntity {
        return AlarmSettingsEntity().apply {
            this.ringToneEnum = RingToneEntity.valueOf(alarmSettings.ringToneMode.name)
            this.alarmModeEnum = AlarmModeEntity.valueOf(alarmSettings.alarmMode.name)
            this.etcSetting = alarmEtcToEntity(alarmSettings.etcSetting)
        }
    }

    fun alarmSettingToModel(
        alarmSettingsEntity: AlarmSettingsEntity
    ): AlarmSettings {
        return AlarmSettings(
            RingTone.valueOf(alarmSettingsEntity.ringToneEnum.state),
            AlarmMode.valueOf(alarmSettingsEntity.alarmModeEnum.state),
            alarmEtcToModel(alarmSettingsEntity.etcSetting ?: AlarmEtcSettingsEntity())
        )
    }

    fun alarmModeToEntity(alarmMode: AlarmModeSetting): AlarmModeSettingEntity {
        val formatter = DateTimeFormatter.ofPattern("HHmm")
        return when(alarmMode) {
            is AlarmModeSetting.Period -> {
                PeriodEntity().apply {
                    this.selectedDate = alarmMode.selectedDate.toRealmList()
                    this.interval = alarmMode.interval
                    this.alarmStartTime = alarmMode.alarmStartTime.format(formatter)
                    this.alarmEndTime = alarmMode.alarmEndTime.format(formatter)
                    this.alarmList = alarmListToEntity(alarmMode.alarmList).toRealmList()
                }
            }
            is AlarmModeSetting.Custom -> {
                CustomEntity().apply {
                    this.selectedDate = alarmMode.selectedDate.toRealmList()
                    this.interval = alarmMode.interval
                    this.alarmList = alarmListToEntity(alarmMode.alarmList).toRealmList()
                }
            }
        }
    }

    fun alarmModeToModel(alarmModeEntity: AlarmModeSettingEntity?): AlarmModeSetting {
        val formatter = DateTimeFormatter.ofPattern("HHmm")
        return when(alarmModeEntity) {
            is PeriodEntity -> {
                AlarmModeSetting.Period(
                    alarmModeEntity.selectedDate,
                    alarmModeEntity.interval,
                    LocalTime.parse(alarmModeEntity.alarmStartTime, formatter),
                    LocalTime.parse(alarmModeEntity.alarmEndTime, formatter),
                    alarmListToModel(alarmModeEntity.alarmList)
                )
            }
            is CustomEntity -> {
                AlarmModeSetting.Custom(
                    alarmModeEntity.selectedDate,
                    alarmModeEntity.interval,
                    alarmListToModel(alarmModeEntity.alarmList)
                )
            }
            else -> {
                AlarmModeSetting.Period()
            }
        }
    }

    fun alarmToEntity(alarm: Alarm): AlarmEntity {
        return AlarmEntity().apply {
            this.alarmId = alarm.alarmId
            this.startTime = alarm.startTime
            this.enabled = alarm.enabled
            this.interval = alarm.interval
        }
    }

    fun alarmToModel(alarm: AlarmEntity): Alarm {
        return Alarm(
            alarm.alarmId,
            alarm.startTime,
            alarm.enabled,
            alarm.interval
        )
    }

//    fun alarmModeToModel(alarmModeEntity: AlarmListEntity): AlarmMode {
//        val formatter = DateTimeFormatter.ofPattern("HHmm")
//        with(alarmModeEntity) {
//            val period = this.period ?: PeriodEntity()
//            val custom = this.custom ?: CustomEntity()
//            return when(alarmModeEnum) {
//                AlarmModeEnum.PERIOD -> {
//                    AlarmMode.Period(
//                        period.selectedDate,
//                        period.interval,
//                        LocalTime.parse(period.alarmStartTime, formatter),
//                        LocalTime.parse(period.alarmEndTime, formatter),
//                        alarmListToModel(period.alarmList)
//                    )
//                }
//                AlarmModeEnum.CUSTOM -> {
//                    AlarmMode.Custom(
//                        custom.selectedDate,
//                        custom.interval,
//                        alarmListToModel(custom.alarmList)
//                    )
//                }
//                else -> {
//                    AlarmMode.Period()
//                }
//            }
//        }
//    }

    private fun alarmEtcToEntity(alarmEtcSettings: AlarmEtcSettings): AlarmEtcSettingsEntity {
        return AlarmEtcSettingsEntity().apply {
            this.stopReachedGoal = alarmEtcSettings.stopReachedGoal
            this.delayTomorrow = alarmEtcSettings.delayTomorrow
        }
    }

    private fun alarmEtcToModel(alarmEtcSettingsEntity: AlarmEtcSettingsEntity): AlarmEtcSettings {
        return AlarmEtcSettings(
            alarmEtcSettingsEntity.stopReachedGoal,
            alarmEtcSettingsEntity.delayTomorrow
        )
    }

    private fun alarmListToEntity(alarmList: List<Alarm>): List<AlarmEntity> {
        val newList = ArrayList<AlarmEntity>()
        alarmList.forEach {
            val entity = alarmToEntity(it)
            newList.add(entity)
        }

        return newList
    }

    private fun alarmListToModel(alarmList: List<AlarmEntity>): List<Alarm> {
        val newList = ArrayList<Alarm>()
        alarmList.forEach {
            val model = alarmToModel(it)
            newList.add(model)
        }

        return newList
    }
}