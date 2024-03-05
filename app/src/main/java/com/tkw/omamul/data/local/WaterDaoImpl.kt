package com.tkw.omamul.data.local

import com.tkw.omamul.util.DateTimeUtils
import com.tkw.omamul.data.WaterDao
import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

class WaterDaoImpl(r: Realm): WaterDao {
    override val realm: Realm = r
    override val clazz: KClass<DayOfWaterEntity> = DayOfWaterEntity::class

    private val testDate = "20240205"
    override suspend fun getCount(): DayOfWaterEntity? {
        return this.findByOne("date == $0", testDate)
    }

    private val countByDate: MutableRealm.() -> DayOfWaterEntity? = {
        this.query(clazz, "date == $0", testDate).first().find()
    }

    override fun getCountFlow(): Flow<ResultsChange<DayOfWaterEntity>> {
        return this.stream(this.findBy("date == $0", testDate))
    }

    override suspend fun addCount() {
        realm.write {
            val query = countByDate()
            query?.dayOfList?.add(
                WaterEntity().apply {
                    amount = 100
                    date = DateTimeUtils.getToday()
                }
            )
        }
    }

    override suspend fun removeCount(obj: WaterEntity) {
        realm.write {
            val query = countByDate()
            query?.dayOfList?.remove(obj)
        }
    }
}