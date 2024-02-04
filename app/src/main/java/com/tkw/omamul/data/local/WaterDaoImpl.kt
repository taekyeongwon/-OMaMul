package com.tkw.omamul.data.local

import com.tkw.omamul.core.util.DateUtils
import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmResults
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

class WaterDaoImpl(private val realm: Realm): WaterDao {
    private val testDate = "20240203"
    override suspend fun query(): Int {
        return realm.query(WaterEntity::class).find().lastOrNull()?.amount ?: 0
    }

    private fun queryList(): DayOfWaterEntity? {
        return realm.query(DayOfWaterEntity::class, "date == $0", testDate).find().firstOrNull()
    }

    override fun <T: RealmObject> queryStream(clazz: KClass<T>): Flow<ResultsChange<T>> {
        return realm.query(clazz, "date == $0", testDate).asFlow()
    }

    override suspend fun addAsync() {

        realm.write {
            val query = queryList()
            if(query == null) {
                val dayOfWaterEntity =
                    DayOfWaterEntity().apply {
                        date = testDate
                    }
                copyToRealm(dayOfWaterEntity)
            } else {
                val entity = findLatest(query)
                entity?.dayOfList?.add(
                    WaterEntity().apply {
                        amount = 100
                        date = DateUtils.getToday()
                    }
                )
            }
//        val count = if(query == 0) {
//            WaterEntity().apply {
//                amount = 100
//            }
//        } else {
//            WaterEntity().apply {
//                amount = query.plus(100)
//            }
//        }


//            copyToRealm(count, UpdatePolicy.ALL)
        }
    }

    override suspend fun removeAsync(obj: WaterEntity) {
        realm.write {
            val query = findLatest(queryList()!!)
            query?.dayOfList?.remove(obj)
//            findLatest(obj)?.also {
//                delete(it)
//            }
        }
    }
}