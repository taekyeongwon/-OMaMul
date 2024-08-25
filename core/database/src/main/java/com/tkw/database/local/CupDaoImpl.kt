package com.tkw.database.local

import com.tkw.database.CupDao
import com.tkw.database.model.CupEntity
import com.tkw.database.model.CupListEntity
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.reflect.KClass

class CupDaoImpl @Inject constructor(): CupDao {
    override val realm: Realm = Realm.open(getRealmConfiguration())

    private val getCupList: MutableRealm.() -> CupListEntity? = {
        this.query(CupListEntity::class, "cupId == $0", CupEntity.DEFAULT_CUP_LIST_ID).first().find()
    }

    override fun getCup(id: String): CupEntity? {
        return this.findFirst(CupListEntity::class)?.cupList?.find { it.cupId == id }
    }

    override fun getCupListFlow(): Flow<ResultsChange<CupListEntity>> {
        return this.stream(this.find(CupListEntity::class, "cupId == $0", CupEntity.DEFAULT_CUP_LIST_ID))
    }

    override suspend fun createList() {
        this.upsert(CupListEntity())
    }

    override suspend fun insertCup(obj: CupEntity) {
        this.write {
            getCupList()?.cupList?.add(obj)
        }
    }

    override suspend fun updateCup(target: CupEntity) {
        this.write {
            val origin = getCup(target.cupId)
            findLatest(origin!!)?.apply {
                cupName = target.cupName
                cupAmount = target.cupAmount
            }
        }
    }

    override suspend fun updateAll(list: List<CupEntity>) {
        this.write {
            getCupList()?.cupList?.clear()
            getCupList()?.cupList?.addAll(list)
        }
    }

    override suspend fun deleteCup(cupId: String) {
        this.write {
            val cup = getCup(cupId)
            getCupList()?.cupList?.remove(cup)
        }
    }
}