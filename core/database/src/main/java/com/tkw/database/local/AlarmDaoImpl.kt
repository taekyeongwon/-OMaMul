package com.tkw.database.local

import android.util.Log
import com.tkw.database.AlarmDao
import com.tkw.database.model.AlarmEntity
import com.tkw.database.model.AlarmListEntity
import com.tkw.database.model.AlarmModeSettingEntity
import com.tkw.database.model.AlarmModeEntity
import com.tkw.database.model.AlarmSettingsEntity
import com.tkw.database.model.CustomAlarmListEntity
import com.tkw.database.model.PeriodAlarmListEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

class AlarmDaoImpl @Inject constructor(): AlarmDao {
    override val realm: Realm = Realm.open(getRealmConfiguration())
    companion object {
        private const val TAG = "AlarmDao"
    }

    private val alarmModeFlow =
        this.find(
            AlarmModeSettingEntity::class,
            "id == $0", AlarmSettingsEntity.DEFAULT_SETTING_ID
        ).asFlow()

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

    init {
        //알람 리스트 로그용
        CoroutineScope(Dispatchers.IO).launch {
            launch {
                getPeriodAlarmListEntity.collectLatest {
                    Log.d(TAG, "PeriodAlarmList : " + it.alarmList.joinToString(",\n"))
                }
            }
            launch {
                getCustomAlarmListEntity.collectLatest {
                    Log.d(TAG, "CustomAlarmList : " + it.alarmList.joinToString(",\n"))
                }
            }
        }
    }

    private suspend fun getPeriodAlarm(alarmId: String): AlarmEntity? {
        Log.d(TAG, "${::getPeriodAlarm.name} alarmId : $alarmId")
        val entity = getPeriodAlarmListEntity.first()
        return entity.alarmList
            .find {
                it.alarmId == alarmId
            }
    }

    private suspend fun getCustomAlarm(alarmId: String): AlarmEntity? {
        Log.d(TAG, "${::getCustomAlarm.name} alarmId : $alarmId")
        val entity = getCustomAlarmListEntity.first()
        return entity.alarmList
            .find {
                it.alarmId == alarmId
            }
    }

    override fun getSetting(): Flow<ResultsChange<AlarmSettingsEntity>> {
        return this.stream(this.find(AlarmSettingsEntity::class, "id == $0", AlarmSettingsEntity.DEFAULT_SETTING_ID))
    }

    override suspend fun updateSetting(setting: AlarmSettingsEntity) {
        Log.d(TAG, "${::updateSetting.name} " +
                "alarmMode : ${setting.alarmModeEnum.name}," +
                "ringtoneMode : ${setting.ringToneMode}," +
                "etcSetting : ${setting.etcSetting}")
        this.upsert(setting)
    }

    override fun getAlarmModeSetting(): Flow<AlarmModeSettingEntity?> {
        return alarmModeFlow.map {
            it.list.firstOrNull()
        }
    }

    override suspend fun updateAlarmModeSetting(alarmModeSettingEntity: AlarmModeSettingEntity) {
        Log.d(TAG, "${::updateAlarmModeSetting} selectedDate : " +
                alarmModeSettingEntity.selectedDate.joinToString(", ") +
                " interval : ${alarmModeSettingEntity.interval}")
        this.upsert(alarmModeSettingEntity)
    }

    override fun getAlarmList(mode: AlarmModeEntity): Flow<AlarmListEntity> {
        return when(mode) {
            AlarmModeEntity.PERIOD -> getPeriodAlarmListEntity
            AlarmModeEntity.CUSTOM -> getCustomAlarmListEntity
        }
    }

    override suspend fun getEnabledAlarmList(alarmMode: AlarmModeEntity): AlarmListEntity {
        Log.d("AlarmDao", "${::getEnabledAlarmList.name} mode : ${alarmMode.name}")
        return when (alarmMode) {
            AlarmModeEntity.PERIOD -> getEnabledPeriodAlarmList()
            AlarmModeEntity.CUSTOM -> getEnabledCustomAlarmList()
        }
    }

    override suspend fun setAlarm(alarm: AlarmEntity, alarmMode: AlarmModeEntity) {
        Log.d("AlarmDao", "${::setAlarm.name} alarmId : ${alarm.alarmId} mode : ${alarmMode.name}")
        val alarmEntity = when(alarmMode) {
            AlarmModeEntity.PERIOD -> {
                getPeriodAlarm(alarm.alarmId)
            }
            AlarmModeEntity.CUSTOM -> {
                getCustomAlarm(alarm.alarmId)
            }
        }
        if(alarmEntity != null) {
            if(alarmEntity != alarm) {
                updateAlarm(alarmEntity, alarm)
            }
        } else {
            this.addAlarm(alarm, alarmMode)
        }
    }

    override suspend fun setAlarmList(list: List<AlarmEntity>, mode: AlarmModeEntity) {
        Log.d("AlarmDao", "${::setAlarmList.name} list size : ${list.size} mode : ${mode.name}")
        val entity = getAlarmList(mode).first()
        this.write {
            val latestObj = when(entity) {
                is PeriodAlarmListEntity -> findLatest(entity)
                is CustomAlarmListEntity -> findLatest(entity)
                else -> null
            }
            latestObj?.alarmList?.clear()
            latestObj?.alarmList?.addAll(list.distinctBy {
                it.alarmId
            })
        }
    }

    private suspend fun updateAlarm(currentAlarm: AlarmEntity, newAlarm: AlarmEntity) {
        Log.d("AlarmDao", "${::updateAlarm.name} current : $currentAlarm, new : $newAlarm ")
        this.write {
            findLatest(currentAlarm)?.apply {
                this.startTime = newAlarm.startTime
                this.selectedDates = newAlarm.selectedDates
                this.enabled = newAlarm.enabled
            }
        }
    }

    private suspend fun addAlarm(alarm: AlarmEntity, alarmMode: AlarmModeEntity) {
        Log.d("AlarmDao", "${::addAlarm.name} alarmId : ${alarm.alarmId}, mode : ${alarmMode.name}")
        when(alarmMode) {
            AlarmModeEntity.PERIOD -> {   //PeriodEntity 조회해서 업데이트
                val period = getPeriodAlarmListEntity.first()
                this.write {
                    findLatest(period)?.apply {
                        alarmList.add(alarm)
                    }
                }
            }
            AlarmModeEntity.CUSTOM -> {   //CustomEntity 조회해서 업데이트
                val custom = getCustomAlarmListEntity.first()
                this.write {
                    findLatest(custom)?.apply {
                        alarmList.add(alarm)
                    }
                }
            }
        }
    }

    override suspend fun cancelAlarm(alarmId: String, mode: AlarmModeEntity) {
        Log.d("AlarmDao", "${::cancelAlarm.name} alarmId : $alarmId, mode : ${mode.name}")
        val entity = when(mode) {
            AlarmModeEntity.PERIOD -> {
                getPeriodAlarm(alarmId)
            }
            AlarmModeEntity.CUSTOM -> {
                getCustomAlarm(alarmId)
            }
        }
        entity?.let {
            this.write {
                findLatest(entity)?.apply {
                    enabled = false
                }
            }
        }
    }

    override suspend fun deleteAlarm(list: List<AlarmEntity>, mode: AlarmModeEntity) {
        Log.d("AlarmDao", "${::deleteAlarm.name} mode : ${mode.name} list : ${list.joinToString(",\n")}")
        val queriedList = when(mode) {
            AlarmModeEntity.PERIOD -> getPeriodAlarmListEntity
            AlarmModeEntity.CUSTOM -> getCustomAlarmListEntity
        }.first()
        this.write {
            findLatest(queriedList)?.apply {
                alarmList.removeAll(list)
            }
        }
    }

    override suspend fun deleteAllAlarm(mode: AlarmModeEntity) {
        Log.d("AlarmDao", "${::deleteAllAlarm.name} mode : ${mode.name}")
        this.write {
            when(mode) {
                AlarmModeEntity.PERIOD -> delete(PeriodAlarmListEntity::class)
                AlarmModeEntity.CUSTOM -> delete(CustomAlarmListEntity::class)
            }
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
}