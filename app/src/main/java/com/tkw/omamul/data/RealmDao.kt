package com.tkw.omamul.data

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface RealmDao<T: RealmObject> {
    val realm: Realm
    val clazz: KClass<T>

    fun findBy(query: String, vararg args: String): RealmResults<T> {
        return realm.query(clazz, query, *args).find()
    }

    fun findByOne(query: String, vararg args: String): T? {
        return realm.query(clazz, query, *args).first().find()
    }

    fun findAll(): RealmResults<T> {
        return realm.query(clazz).find()
    }

    fun findLast(): T? {
        return realm.query(clazz).find().lastOrNull()
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