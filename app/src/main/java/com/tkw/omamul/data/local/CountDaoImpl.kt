package com.tkw.omamul.data.local

import com.tkw.omamul.data.model.CountEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

class CountDaoImpl(private val realm: Realm): CountDao {
    override suspend fun query(): Int {
        return realm.query(CountEntity::class, "id == $0", 0).first().find()?.count ?: -1
    }

    override suspend fun queryStream(): Flow<ResultsChange<CountEntity>>? {
        return null
        //쿼리 결과 계속 확인 가능한지 확인 필요
    }

    override suspend fun addAsync() {
        val query = query()
        val count = if(query == -1) {
            CountEntity().apply {
                id = 0
                count = 0
            }
        } else {
            CountEntity().apply {
                id = 0
                count = query.plus(1)
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