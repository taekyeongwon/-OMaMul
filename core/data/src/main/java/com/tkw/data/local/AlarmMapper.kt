package com.tkw.data.local

import com.tkw.database.model.AlarmEntity
import com.tkw.database.model.AlarmEtcSettingsEntity
import com.tkw.database.model.AlarmModeEntity
import com.tkw.database.model.AlarmSettingsEntity
import com.tkw.database.model.CustomEntity
import com.tkw.database.model.PeriodEntity
import com.tkw.database.model.RingToneEntity
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmEtcSettings
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
            this.alarmMode = alarmModeToEntity(alarmSettings.alarmMode)
            this.etcSetting = alarmEtcToEntity(alarmSettings.etcSetting)
        }
    }

    fun alarmSettingToModel(
        alarmSettingsEntity: AlarmSettingsEntity
    ): AlarmSettings {
        return AlarmSettings(
            RingTone.valueOf(alarmSettingsEntity.ringToneEnum.state),
            alarmModeToModel(alarmSettingsEntity.alarmMode),
            alarmEtcToModel(alarmSettingsEntity.etcSetting)
        )
    }

    fun alarmToEntity(alarm: Alarm): AlarmEntity {
        return AlarmEntity().apply {
            this.alarmId = alarm.alarmId
            this.startTime = alarm.startTime
            this.enabled = alarm.enabled
        }
    }

    fun alarmToModel(alarm: AlarmEntity): Alarm {
        return Alarm(
            alarm.alarmId,
            alarm.startTime,
            alarm.enabled
        )
    }

    private fun alarmModeToEntity(alarmMode: AlarmMode): AlarmModeEntity {
        val formatter = DateTimeFormatter.ofPattern("HHmm")
        return when(alarmMode) {
            is AlarmMode.Period -> {
                PeriodEntity().apply {
                    this.selectedDate = alarmMode.selectedDate.toRealmList()
                    this.interval = alarmMode.interval
                    this.alarmStartTime = alarmMode.alarmStartTime.format(formatter)
                    this.alarmEndTime = alarmMode.alarmEndTime.format(formatter)
                    this.alarm = alarmToEntity(alarmMode.alarm)
                }
            }
            is AlarmMode.Custom -> {
                CustomEntity().apply {
                    this.selectedDate = alarmMode.selectedDate.toRealmList()
                    this.alarmList = alarmListToEntity(alarmMode.alarmList).toRealmList()
                }
            }
        }
    }

    private fun alarmModeToModel(alarmModeEntity: AlarmModeEntity?): AlarmMode {
        val formatter = DateTimeFormatter.ofPattern("HHmm")
        return when(alarmModeEntity) {
            is PeriodEntity -> {
                AlarmMode.Period(
                    alarmModeEntity.selectedDate,
                    alarmModeEntity.interval,
                    LocalTime.parse(alarmModeEntity.alarmStartTime, formatter),
                    LocalTime.parse(alarmModeEntity.alarmEndTime, formatter),
                    alarmToModel(alarmModeEntity.alarm)
                )
            }
            is CustomEntity -> {
                AlarmMode.Custom(
                    alarmModeEntity.selectedDate,
                    alarmListToModel(alarmModeEntity.alarmList)
                )
            }
            else -> {
                AlarmMode.Period()
            }
        }
    }

    private fun alarmEtcToEntity(alarmEtcSettings: AlarmEtcSettings): AlarmEtcSettingsEntity {
        return AlarmEtcSettingsEntity().apply {
            this.stopReachedGoal = alarmEtcSettings.stopReachedGoal
            this.delayTomorrow = alarmEtcSettings.delayTomorrow
        }
    }

    private fun alarmEtcToModel(alarmEtcSettingsEntity: AlarmEtcSettingsEntity?): AlarmEtcSettings {
        return AlarmEtcSettings(
            alarmEtcSettingsEntity?.stopReachedGoal ?: false,
            alarmEtcSettingsEntity?.delayTomorrow ?: false
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