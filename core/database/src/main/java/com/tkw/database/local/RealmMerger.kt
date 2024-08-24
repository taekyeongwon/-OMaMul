package com.tkw.database.local

import android.util.Log
import com.tkw.database.FileMerger
import com.tkw.database.model.AlarmEntity
import com.tkw.database.model.AlarmEtcSettingsEntity
import com.tkw.database.model.AlarmModeSettingEntity
import com.tkw.database.model.AlarmSettingsEntity
import com.tkw.database.model.CupEntity
import com.tkw.database.model.CupListEntity
import com.tkw.database.model.CustomAlarmListEntity
import com.tkw.database.model.DayOfWaterEntity
import com.tkw.database.model.PeriodAlarmListEntity
import com.tkw.database.model.RingToneModeEntity
import com.tkw.database.model.SettingEntity
import com.tkw.database.model.WaterEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.copyFromRealm
import java.io.File
import javax.inject.Inject

class RealmMerger @Inject constructor(): FileMerger {
    private val realmEntities = setOf(
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

    override suspend fun onMerge(sourceFile: File, destFile: File) {
        val tmpRealmConfig = RealmConfiguration.Builder(realmEntities)
            .name(sourceFile.name)
            .deleteRealmIfMigrationNeeded()
            .build()
        val defaultRealmConfig = RealmConfiguration.Builder(realmEntities)
            .name(destFile.name)
            .deleteRealmIfMigrationNeeded()
            .build()
        val sourceRealm = Realm.open(tmpRealmConfig)
        val destRealm = Realm.open(defaultRealmConfig)

        mergeWaterDB(sourceRealm, destRealm)
        mergeCupDB(sourceRealm, destRealm)
        mergeAlarmDB(sourceRealm, destRealm)
        mergeSettingDB(sourceRealm, destRealm)

        sourceRealm.close()
        destRealm.close()
        Realm.deleteRealm(tmpRealmConfig)
        Log.d("RealmMerger", "Merge complete.")
    }

    private suspend fun mergeWaterDB(sourceRealm: Realm, destRealm: Realm) {
        val dest = destRealm.query(DayOfWaterEntity::class).find()
        sourceRealm.query(DayOfWaterEntity::class).find().forEach { sourceEntity ->
            val destEntity = dest.find { it.date == sourceEntity.date }
            destRealm.write {
                val copiedSource = sourceEntity.copyFromRealm()
                if(destEntity != null) {
                    copiedSource.dayOfList.addAll(destEntity.dayOfList)
                    val distinctList = copiedSource.dayOfList.distinctBy { it.dateTime }
                    val newEntity = DayOfWaterEntity().apply {
                        this.date = destEntity.date
                        this.dayOfList.addAll(distinctList)
                    }
                    copyToRealm(newEntity, UpdatePolicy.ALL)
                } else {
                    copyToRealm(copiedSource)
                }
            }
        }
    }

    private suspend fun mergeCupDB(sourceRealm: Realm, destRealm: Realm) {
        val destEntity = destRealm.query(
            CupListEntity::class,
            "cupId == $0",
            CupEntity.DEFAULT_CUP_LIST_ID
        ).find().firstOrNull()
        val sourceEntity = sourceRealm.query(CupListEntity::class).find().firstOrNull()

        if(sourceEntity != null) {
            destRealm.write {
                val copiedSource = sourceEntity.copyFromRealm()
                if(destEntity != null) {
                    copiedSource.cupList.addAll(destEntity.cupList)
                    val distinctList = copiedSource.cupList.distinctBy { it.cupId }
                    val newEntity = CupListEntity().apply {
                        this.cupList.addAll(distinctList)
                    }
                    copyToRealm(newEntity, UpdatePolicy.ALL)
                } else {
                    copyToRealm(copiedSource)
                }
            }
        }
    }

    private suspend fun mergeAlarmDB(sourceRealm: Realm, destRealm: Realm) {
        mergeAlarmSetting(sourceRealm, destRealm)
        mergePeriodAlarm(sourceRealm, destRealm)
        mergeCustomAlarm(sourceRealm, destRealm)
    }

    private suspend fun mergeSettingDB(sourceRealm: Realm, destRealm: Realm) {
        val source = sourceRealm.query(
            SettingEntity::class,
            "id == $0", 0
        ).find().firstOrNull()

        if(source != null) {
            destRealm.write {
                copyToRealm(source.copyFromRealm(), UpdatePolicy.ALL)
            }
        }
    }

    private suspend fun mergeAlarmSetting(sourceRealm: Realm, destRealm: Realm) {
        val settingEntity = sourceRealm.query(
            AlarmSettingsEntity::class,
            "id == $0",
            AlarmSettingsEntity.DEFAULT_SETTING_ID
        ).find().firstOrNull()
        if(settingEntity != null) {
            destRealm.write {
                copyToRealm(settingEntity.copyFromRealm(), UpdatePolicy.ALL)
            }
        }

        val modeSettingEntity = sourceRealm.query(
            AlarmModeSettingEntity::class,
            "id == $0",
            AlarmSettingsEntity.DEFAULT_SETTING_ID
        ).find().firstOrNull()

        if(modeSettingEntity != null) {
            destRealm.write {
                copyToRealm(modeSettingEntity.copyFromRealm(), UpdatePolicy.ALL)
            }
        }
    }

    private suspend fun mergePeriodAlarm(sourceRealm: Realm, destRealm: Realm) {
        val periodEntity = sourceRealm.query(
            PeriodAlarmListEntity::class,
            "id == $0",
            AlarmSettingsEntity.DEFAULT_SETTING_ID
        ).find().firstOrNull()

        if(periodEntity != null) {
            destRealm.write {
                copyToRealm(periodEntity.copyFromRealm(), UpdatePolicy.ALL)
            }
        }
    }

    private suspend fun mergeCustomAlarm(sourceRealm: Realm, destRealm: Realm) {
        val customEntity = sourceRealm.query(
            CustomAlarmListEntity::class,
            "id == $0",
            AlarmSettingsEntity.DEFAULT_SETTING_ID
        ).find().firstOrNull()

        if(customEntity != null) {
            destRealm.write {
                copyToRealm(customEntity.copyFromRealm(), UpdatePolicy.ALL)
            }
        }
    }
}