package com.tkw.database.local

import com.tkw.database.AlarmDao
import com.tkw.database.model.AlarmEntity
import com.tkw.database.model.AlarmListEntity
import com.tkw.database.model.AlarmModeSettingEntity
import com.tkw.database.model.AlarmModeEntity
import com.tkw.database.model.AlarmSettingsEntity
import com.tkw.database.model.CustomAlarmListEntity
import com.tkw.database.model.CustomEntity
import com.tkw.database.model.PeriodAlarmListEntity
import com.tkw.database.model.PeriodEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.reflect.KClass

class AlarmDaoImpl @Inject constructor(): AlarmDao {
    override val realm: Realm = Realm.open(getRealmConfiguration())
    override val clazz: KClass<AlarmSettingsEntity> = AlarmSettingsEntity::class

    private fun getAlarmMode(): AlarmModeEntity {
        val alarmMode = this.findBy(
            "id == $0", AlarmSettingsEntity.DEFAULT_SETTING_ID
        ).firstOrNull()
        return alarmMode?.alarmModeEnum ?: AlarmModeEntity.PERIOD
    }

    private val getPeriodAlarmListEntity: Flow<PeriodAlarmListEntity> = flow {
        val query = find(
            PeriodAlarmListEntity::class,
            "id == $0", AlarmSettingsEntity.DEFAULT_SETTING_ID
        ).asFlow()

        query.collect {
            val entity = it.list.firstOrNull()
            if (entity == null) {
                upsert(PeriodAlarmListEntity())
            } else {
                emit(entity)
            }
        }
    }

    private val getCustomAlarmListEntity: Flow<CustomAlarmListEntity> = flow {
        val query = find(
            CustomAlarmListEntity::class,
            "id == $0", AlarmSettingsEntity.DEFAULT_SETTING_ID
        ).asFlow()

        query.collect {
            val entity = it.list.firstOrNull()
            if(entity == null) {
                upsert(CustomAlarmListEntity())
            } else {
                emit(entity)
            }
        }
    }

    private suspend fun getPeriodAlarm(alarmId: Int): AlarmEntity? {
        val entity = getPeriodAlarmListEntity.first()
        return entity.alarmList
            .find {
                it.alarmId == alarmId
            }
    }

    private suspend fun getCustomAlarm(alarmId: Int): AlarmEntity? {
        val entity = getCustomAlarmListEntity.first()
        return entity.alarmList
            .find {
                it.alarmId == alarmId
            }
    }

    private val periodFlow =
        this.find(
            PeriodEntity::class,
            "id == $0", AlarmSettingsEntity.DEFAULT_SETTING_ID
        ).asFlow()

    private val customFlow =
        this.find(
            CustomEntity::class,
            "id == $0", AlarmSettingsEntity.DEFAULT_SETTING_ID
        ).asFlow()

    override suspend fun updateSetting(setting: AlarmSettingsEntity) {
        this.upsert(setting)
    }

    override fun getSetting(): Flow<ResultsChange<AlarmSettingsEntity>> {
        return this.stream(this.findBy("id == $0", AlarmSettingsEntity.DEFAULT_SETTING_ID))
    }

    override suspend fun updateAlarm(alarm: AlarmEntity) {
        val alarmMode = getAlarmMode()
        val alarmEntity = when(alarmMode) {
            AlarmModeEntity.PERIOD -> {
                getPeriodAlarm(alarm.alarmId)
            }
            AlarmModeEntity.CUSTOM -> {
                getCustomAlarm(alarm.alarmId)
            }
        }
        if(alarmEntity != null) {
            updateAlarm(alarmEntity, alarm)
        } else {
            setAlarm(alarm)
        }
    }

    private suspend fun updateAlarm(from: AlarmEntity, to: AlarmEntity) {
        realm.write {
            findLatest(from)?.apply {
                this.startTime = to.startTime
                this.enabled = to.enabled
            }
        }
    }

    private suspend fun setAlarm(alarm: AlarmEntity) {
        val alarmMode = getAlarmMode()
        when(alarmMode) {
            AlarmModeEntity.PERIOD -> {   //PeriodEntity 조회해서 업데이트
                val period = getPeriodAlarmListEntity.first()
                realm.write {
                    findLatest(period)?.apply {
                        alarmList.add(alarm)
                    }
                }
            }
            AlarmModeEntity.CUSTOM -> {   //CustomEntity 조회해서 업데이트
                val custom = getCustomAlarmListEntity.first()
                realm.write {
                    findLatest(custom)?.apply {
                        alarmList.add(alarm)
                    }
                }
            }
        }
    }

    override suspend fun cancelAlarm(alarmId: Int) {
        val alarm = this.find(AlarmEntity::class, "alarmId == $0", alarmId).firstOrNull()
        alarm?.let {
            realm.write {
                findLatest(alarm)?.apply {
                    enabled = false
                }
            }
        }
    }

    override suspend fun updateAlarmModeSetting(alarmModeSettingEntity: AlarmModeSettingEntity) {
        when(alarmModeSettingEntity) {
            is PeriodEntity -> {
                this.upsert(alarmModeSettingEntity)
            }
            is CustomEntity -> {
                this.upsert(alarmModeSettingEntity)
            }
            else -> {
                //nothing
            }
        }
    }

    override fun getAlarmModeSetting(mode: AlarmModeEntity): Flow<AlarmModeSettingEntity?> {
        return when (mode) {
            AlarmModeEntity.PERIOD -> {
                periodFlow.map {
                    it.list.firstOrNull()
                }
            }
            AlarmModeEntity.CUSTOM -> {
                customFlow.map {
                    it.list.firstOrNull()
                }
            }
        }
    }

    override fun getAlarmList(): Flow<AlarmListEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun getEnabledAlarmList(): AlarmListEntity {
        val alarmMode = getAlarmMode()
        return when (alarmMode) {
            AlarmModeEntity.PERIOD -> getEnabledPeriodAlarmList()
            AlarmModeEntity.CUSTOM -> getEnabledCustomAlarmList()
        }
    }

    private suspend fun getEnabledPeriodAlarmList(): PeriodAlarmListEntity {
        val period = getPeriodAlarmListEntity.first()
        val enableList = period.alarmList.filter { it.enabled }
        return copyFromRealm(period)?.apply {
            this.alarmList.clear()
            this.alarmList.addAll(enableList)
        } ?: PeriodAlarmListEntity()
    }

    private suspend fun getEnabledCustomAlarmList(): CustomAlarmListEntity {
        val custom = getCustomAlarmListEntity.first()
        val enableList = custom.alarmList.filter { it.enabled }
        return copyFromRealm(custom)?.apply {
            this.alarmList.clear()
            this.alarmList.addAll(enableList)
        } ?: CustomAlarmListEntity()
    }

    override suspend fun deleteAlarm(alarmId: Int) {
        realm.write {
            val entity = this.query<AlarmEntity>("alarmId == $0", alarmId)
            delete(entity)
        }
    }

    override suspend fun allDelete(mode: AlarmModeEntity) {
        realm.write {
            when(mode) {
                AlarmModeEntity.PERIOD -> delete(PeriodAlarmListEntity::class)
                AlarmModeEntity.CUSTOM -> delete(CustomAlarmListEntity::class)
            }
        }
    }
}