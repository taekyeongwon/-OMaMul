package com.tkw.data.local

import com.tkw.database.model.AlarmEntity
import com.tkw.database.model.AlarmEtcSettingsEntity
import com.tkw.database.model.AlarmListEntity
import com.tkw.database.model.AlarmModeSettingEntity
import com.tkw.database.model.AlarmModeEntity
import com.tkw.database.model.AlarmSettingsEntity
import com.tkw.database.model.CustomEntity
import com.tkw.database.model.PeriodEntity
import com.tkw.database.model.RingToneModeEntity
import com.tkw.domain.model.Alarm
import com.tkw.domain.model.AlarmEtcSettings
import com.tkw.domain.model.AlarmList
import com.tkw.domain.model.AlarmModeSetting
import com.tkw.domain.model.AlarmMode
import com.tkw.domain.model.AlarmSettings
import com.tkw.domain.model.RingToneMode
import io.realm.kotlin.ext.toRealmList
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object AlarmMapper {

    fun alarmSettingToEntity(
        alarmSettings: AlarmSettings
    ): AlarmSettingsEntity {
        return AlarmSettingsEntity().apply {
            this.ringToneMode = ringToneToEntity(alarmSettings.ringToneMode)
            this.alarmModeEnum = AlarmModeEntity.valueOf(alarmSettings.alarmMode.name)
            this.etcSetting = alarmEtcToEntity(alarmSettings.etcSetting)
        }
    }

    fun alarmSettingToModel(
        alarmSettingsEntity: AlarmSettingsEntity
    ): AlarmSettings {
        return AlarmSettings(
            ringToneToModel(alarmSettingsEntity.ringToneMode ?: RingToneModeEntity()),
            AlarmMode.valueOf(alarmSettingsEntity.alarmModeEnum.name),
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
                }
            }
            is AlarmModeSetting.Custom -> {
                CustomEntity().apply {
                    this.selectedDate = alarmMode.selectedDate.toRealmList()
                    this.interval = alarmMode.interval
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
                    LocalTime.parse(alarmModeEntity.alarmEndTime, formatter)
                )
            }
            is CustomEntity -> {
                AlarmModeSetting.Custom(
                    alarmModeEntity.selectedDate,
                    alarmModeEntity.interval
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
            this.interval = alarm.interval
            this.enabled = alarm.enabled
        }
    }

    fun alarmToModel(alarm: AlarmEntity): Alarm {
        return Alarm(
            alarm.alarmId,
            alarm.startTime,
            alarm.interval,
            alarm.enabled
        )
    }

    fun alarmModeToEntity(mode: AlarmMode): AlarmModeEntity {
        return AlarmModeEntity.valueOf(mode.name)
    }

    fun alarmListToModel(alarmListEntity: AlarmListEntity): AlarmList {
        val newList = ArrayList<Alarm>()
        alarmListEntity.alarmList.forEach {
            val model = alarmToModel(it)
            newList.add(model)
        }

        return AlarmList(alarmList = newList)
    }

    private fun ringToneToEntity(ringToneMode: RingToneMode): RingToneModeEntity {
        return RingToneModeEntity().apply {
            this.isBell = ringToneMode.isBell
            this.isVibe = ringToneMode.isVibe
            this.isDevice = ringToneMode.isDevice
            this.isSilence = ringToneMode.isSilence
        }
    }

    private fun ringToneToModel(ringToneEntity: RingToneModeEntity): RingToneMode {
        return RingToneMode(
            isBell = ringToneEntity.isBell,
            isVibe = ringToneEntity.isVibe,
            isDevice = ringToneEntity.isDevice,
            isSilence = ringToneEntity.isSilence
        )
    }

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
}