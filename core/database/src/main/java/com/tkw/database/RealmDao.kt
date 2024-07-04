package com.tkw.database

import com.tkw.database.model.AlarmEtcSettingsEntity
import com.tkw.database.model.AlarmModeEntity
import com.tkw.database.model.AlarmSettingsEntity
import com.tkw.database.model.CupEntity
import com.tkw.database.model.CupListEntity
import com.tkw.database.model.DayOfWaterEntity
import com.tkw.database.model.WaterEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface RealmDao<T: RealmObject> {
    val realm: Realm
    val clazz: KClass<T>

    fun getRealmConfiguration(): RealmConfiguration {
        return RealmConfiguration.Builder(setOf(
            DayOfWaterEntity::class,
            WaterEntity::class,
            CupListEntity::class,
            CupEntity::class,
            AlarmSettingsEntity::class,
            AlarmModeEntity::class,
            AlarmEtcSettingsEntity::class
        ))
            .deleteRealmIfMigrationNeeded()
            .build()
    }

    fun findBy(query: String, vararg args: Any): RealmResults<T> {
        return realm.query(clazz, query, *args).find()
    }

    fun findByOne(query: String, vararg args: Any): T? {
        return realm.query(clazz, query, *args).first().find()
    }

    fun findAll(): RealmResults<T> {
        return realm.query(clazz).find()
    }

    fun findFirst(): T? {
        return realm.query(clazz).first().find()
    }

    fun stream(query: RealmResults<T>): Flow<ResultsChange<T>> {
        return query.asFlow()
    }

    suspend fun insert(entity: T) {
        realm.write {
            copyToRealm(entity)
        }
    }

    suspend fun upsert(entity: T) {
        realm.write {
            copyToRealm(entity, UpdatePolicy.ALL)
        }
    }

    suspend fun delete(entity: T) {
        realm.write {
            findLatest(entity)?.let {
                delete(it)
            }
        }
    }

    suspend fun deleteAll() {
        realm.write {
            val allOfClass = query(clazz).find()
            delete(allOfClass)
        }
    }

    suspend fun clear() {
        realm.write {
            deleteAll()
        }
    }
}