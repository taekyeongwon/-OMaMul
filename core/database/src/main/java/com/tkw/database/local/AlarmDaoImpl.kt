package com.tkw.database.local

import com.tkw.database.AlarmDao
import com.tkw.database.model.AlarmEntity
import com.tkw.database.model.AlarmModeSettingEntity
import com.tkw.database.model.AlarmModeEntity
import com.tkw.database.model.AlarmSettingsEntity
import com.tkw.database.model.CustomEntity
import com.tkw.database.model.PeriodEntity
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.flow.Flow
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

    private fun getPeriodEntity(): RealmResults<PeriodEntity> =
        this.find(
            PeriodEntity::class,
            "id == $0", AlarmSettingsEntity.DEFAULT_SETTING_ID
        )

    private fun getCustomEntity(): RealmResults<CustomEntity> =
        this.find(
            CustomEntity::class,
            "id == $0", AlarmSettingsEntity.DEFAULT_SETTING_ID
        )

    override suspend fun updateSetting(setting: AlarmSettingsEntity) {
        this.upsert(setting)
    }

    override fun getSetting(): Flow<ResultsChange<AlarmSettingsEntity>> {
        return this.stream(this.findBy("id == $0", AlarmSettingsEntity.DEFAULT_SETTING_ID))
    }

    override suspend fun updateAlarm(alarm: AlarmEntity) {
        realm.write {
            val entity = this.query<AlarmEntity>("alarmId == $0", alarm.alarmId).first().find()
            //todo 해당 쿼리로 period랑 custom 구분 없이 id로만 가져오는게 가능한건지 확인 필요.
            //만약 period랑 custom에 들어간 알람 객체의 id가 동일하다면?

            if(entity != null) {
                updateAlarm(entity, alarm)
            } else {
                setAlarm(alarm)
            }
        }
    }

    private fun MutableRealm.updateAlarm(from: AlarmEntity, to: AlarmEntity) {
        findLatest(to)?.apply {
            this.startTime = from.startTime
            this.enabled = from.enabled
        }
    }

    private fun MutableRealm.setAlarm(alarm: AlarmEntity) {
        val alarmMode = getAlarmMode()
        when(alarmMode) {
            AlarmModeEntity.PERIOD -> {   //PeriodEntity 조회해서 업데이트
                val period = getPeriodEntity().firstOrNull() ?: PeriodEntity()
                period.alarmList.add(alarm)
                this.upsert(period)
            }
            AlarmModeEntity.CUSTOM -> {   //CustomEntity 조회해서 업데이트
                val custom = getCustomEntity().firstOrNull() ?: CustomEntity()
                custom.alarmList.add(alarm)
                this.upsert(custom)
            }
        }
    }

    override suspend fun cancelAlarm(alarmId: Int) {
        val alarm = this.find(AlarmEntity::class, "alarmId == $0", alarmId).firstOrNull()
        alarm?.enabled = false
        realm.write {
            alarm?.let { setAlarm(it) }
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
                getPeriodEntity().asFlow().map {
                    it.list.firstOrNull()
                }
            }
            AlarmModeEntity.CUSTOM -> {
                getCustomEntity().asFlow().map {
                    it.list.firstOrNull()
                }
            }
        }
    }

    override fun getEnabledAlarmModeSetting(): AlarmModeSettingEntity? {
        val alarmMode = getAlarmMode()
        return when (alarmMode) {
            AlarmModeEntity.PERIOD -> {
                val period = getPeriodEntity().firstOrNull()
                val enableList = period?.alarmList?.filter { it.enabled } ?: listOf()
                copyFromRealm(period).apply {
                    this?.alarmList?.clear()
                    this?.alarmList?.addAll(enableList)
                }
            }
            AlarmModeEntity.CUSTOM -> {
                val custom = getCustomEntity().firstOrNull()
                val enableList = custom?.alarmList?.filter { it.enabled } ?: listOf()
                copyFromRealm(custom).apply {
                    this?.alarmList?.clear()
                    this?.alarmList?.addAll(enableList)
                }
            }
        }
    }

    override suspend fun deleteAlarm(alarmId: Int) {
        realm.write {
            val entity = this.query<AlarmEntity>("alarmId == $0", alarmId).first().find()
            entity?.let { delete(it) }
        }
    }

    override suspend fun allDelete() {
        realm.write {
            delete(PeriodEntity::class)
        }
    }
}