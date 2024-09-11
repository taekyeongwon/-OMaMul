package com.tkw.database

import com.tkw.database.model.RealmClassSet
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface RealmDao {
    val realm: Realm

    fun getRealmConfiguration(): RealmConfiguration {
        return RealmConfiguration.Builder(RealmClassSet.realmEntities)
            .deleteRealmIfMigrationNeeded()
            .build()
    }

    fun <T: RealmObject> find(clazz: KClass<T>, query: String, vararg args: Any): RealmResults<T> {
        return realm.query(clazz, query, *args).find()
    }

    fun <T: RealmObject> findByOne(clazz: KClass<T>, query: String, vararg args: Any): T? {
        return realm.query(clazz, query, *args).first().find()
    }

    fun <T: RealmObject> findAll(clazz: KClass<T>): RealmResults<T> {
        return realm.query(clazz).find()
    }

    fun <T: RealmObject> findFirst(clazz: KClass<T>): T? {
        return realm.query(clazz).first().find()
    }

    fun <T: RealmObject> stream(query: RealmResults<T>): Flow<ResultsChange<T>> {
        return query.asFlow()
    }

    fun <T: RealmObject> copyFromRealm(obj: T?): T? {
        return if(obj != null) realm.copyFromRealm(obj)
        else null
    }

    suspend fun write(block: MutableRealm.() -> Unit) {
        realm.write(block)
    }

    suspend fun <T: RealmObject> insert(entity: T) {
        realm.write {
            copyToRealm(entity)
        }
    }

    suspend fun <T: RealmObject> upsert(entity: T) {
        realm.write {
            copyToRealm(entity, UpdatePolicy.ALL)
        }
    }

    fun <T: RealmObject> MutableRealm.upsert(entity: T) {
        copyToRealm(entity, UpdatePolicy.ALL)
    }

    suspend fun <T: RealmObject> delete(entity: T) {
        realm.write {
            findLatest(entity)?.let {
                delete(it)
            }
        }
    }

    suspend fun <T: RealmObject> deleteAll(clazz: KClass<T>) {
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