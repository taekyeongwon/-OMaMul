package com.tkw.omamul.data.local

import android.util.Log
import com.tkw.omamul.data.model.CountEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.asFlow
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

class CountDaoImpl(private val realm: Realm): CountDao {
    override suspend fun query(): Int {
        return realm.query(CountEntity::class).find().lastOrNull()?.count ?: 0
    }

    override fun <T: RealmObject> queryStream(clazz: KClass<T>): Flow<ResultsChange<T>> {
        return realm.query(clazz).asFlow()
    }

    override suspend fun addAsync() {
        val query = query()
        val count = if(query == 0) {
            CountEntity().apply {
                count = 100
            }
        } else {
            CountEntity().apply {
                count = query.plus(100)
            }
        }

        realm.write {
            copyToRealm(count, UpdatePolicy.ALL)
        }
    }

    override suspend fun removeAsync() {
        //위에 id를 auto increment로 하고 쌓이는거 확인 후 delete 처리
    }
}