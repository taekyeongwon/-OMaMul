package com.tkw.omamul.data.local

import com.tkw.omamul.data.WaterDao
import com.tkw.omamul.data.model.DayOfWaterEntity
import com.tkw.omamul.data.model.WaterEntity
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.query.RealmResults
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

class WaterDaoImpl(r: Realm): WaterDao {
    override val realm: Realm = r
    override val clazz: KClass<DayOfWaterEntity> = DayOfWaterEntity::class

    private val amountByDate: MutableRealm.(String) -> DayOfWaterEntity? = {
        this.query(clazz, "date == $0", it).first().find()
    }

    override suspend fun getDayOfWater(date: String): DayOfWaterEntity? {
        return this.findByOne("date == $0", date)
    }

    override fun getAllDayOfWater(): Flow<ResultsChange<DayOfWaterEntity>> {
        return this.stream(this.findAll())
    }

    override suspend fun getWater(date: String, time: String): WaterEntity? {
        return this.findByOne("date == $0", date)?.dayOfList?.find { it.dateTime == time }
    }

    override fun getAmountFlow(date: String): Flow<ResultsChange<DayOfWaterEntity>> {
        return this.stream(this.findBy("date == $0", date))
    }

    override suspend fun addAmount(date: String, newObj: WaterEntity) {
        realm.write {
            val query = amountByDate(date)
            query?.dayOfList?.add(newObj)
        }
    }

    override suspend fun removeAmount(date: String, obj: WaterEntity) {
        realm.write {
            val query = amountByDate(date)
            query?.dayOfList?.remove(obj)
        }
    }

    override suspend fun updateAmount(origin: WaterEntity, target: WaterEntity) {
        realm.write {
            findLatest(origin)?.apply {
                amount = target.amount
                dateTime = target.dateTime
            }
        }
    }
}