package com.tkw.database.local

import com.tkw.database.WaterDao
import com.tkw.database.model.DayOfWaterEntity
import com.tkw.database.model.WaterEntity
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

class WaterDaoImpl: WaterDao {
    override val realm: Realm = Realm.open(getRealmConfiguration())
    override val clazz: KClass<DayOfWaterEntity> = DayOfWaterEntity::class

    private val amountByDate: MutableRealm.(String) -> DayOfWaterEntity? = {
        this.query(clazz, "date == $0", it).first().find()
    }

    override fun getDayOfWater(date: String): DayOfWaterEntity? {
        return this.findByOne("date == $0", date)
    }

    override fun getAllDayOfWater(): Flow<ResultsChange<DayOfWaterEntity>> {
        return this.stream(this.findAll())
    }

    override fun getWater(date: String, dateTime: String): WaterEntity? {
        return this.findByOne("date == $0", date)?.dayOfList?.findLast { it.dateTime == dateTime }
    }

    override fun getAmountFlow(date: String): Flow<ResultsChange<DayOfWaterEntity>> {
        return this.stream(this.findBy("date == $0", date))
    }

    override fun getAmountDuring(start: String, end: String): List<DayOfWaterEntity> {
        return this.findBy("$0 <= date AND date <= $1", start, end)
    }

    override suspend fun addAmount(date: String, newObj: WaterEntity) {
        realm.write {
            val query = amountByDate(date)
            query?.dayOfList?.add(newObj)
        }
    }

    override suspend fun removeAmount(selectedDate: String, dateTime: String) {
        realm.write {
            val water = getWater(selectedDate, dateTime)
            val query = amountByDate(selectedDate)
            query?.dayOfList?.remove(water)
        }
    }

    override suspend fun updateAmount(
        selectedDate: String,
        origin: WaterEntity,
        target: WaterEntity
    ) {
        realm.write {
            val originWater = getWater(selectedDate, origin.dateTime)
            findLatest(originWater!!)?.apply {
                amount = target.amount
                dateTime = target.dateTime
            }
        }
    }
}