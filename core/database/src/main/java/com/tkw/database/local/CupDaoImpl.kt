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
    override val clazz: KClass<CupListEntity> = CupListEntity::class

    private val getCupList: MutableRealm.() -> CupListEntity? = {
        this.query(clazz, "cupId == $0", CupEntity.DEFAULT_CUP_LIST_ID).first().find()
    }

    override fun getCup(id: String): CupEntity? {
        return this.findFirst()?.cupList?.find { it.cupId == id }
    }

    override fun getCupListFlow(): Flow<ResultsChange<CupListEntity>> {
        return this.stream(this.findBy("cupId == $0", CupEntity.DEFAULT_CUP_LIST_ID))
    }

    override suspend fun createList() {
        this.upsert(CupListEntity())
    }

    override suspend fun insertCup(obj: CupEntity) {
        realm.write {
            getCupList()?.cupList?.add(obj)
        }
    }

    override suspend fun updateCup(target: CupEntity) {
        realm.write {
            val origin = getCup(target.cupId)
            findLatest(origin!!)?.apply {
                cupName = target.cupName
                cupAmount = target.cupAmount
            }
        }
    }

    override suspend fun updateAll(list: List<CupEntity>) {
        realm.write {
            getCupList()?.cupList?.clear()
            getCupList()?.cupList?.addAll(list)
        }
    }

    override suspend fun deleteCup(cupId: String) {
        realm.write {
            val cup = getCup(cupId)
            getCupList()?.cupList?.remove(cup)
        }
    }
}