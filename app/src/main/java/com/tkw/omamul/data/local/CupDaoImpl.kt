package com.tkw.omamul.data.local

import com.tkw.omamul.data.CupDao
import com.tkw.omamul.data.model.CupEntity
import io.realm.kotlin.Realm
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

class CupDaoImpl(r: Realm): CupDao {
    override val realm: Realm = r
    override val clazz: KClass<CupEntity> = CupEntity::class

    override fun getCupListFlow(): Flow<ResultsChange<CupEntity>> {
        return this.stream(this.findAll())
    }

    override suspend fun insertCup(obj: CupEntity) {
        this.insert(obj)
    }

    override suspend fun updateCup(obj: CupEntity) {
        this.upsert(obj)
    }

    override suspend fun deleteCup(obj: CupEntity) {
        this.delete(obj)
    }
}