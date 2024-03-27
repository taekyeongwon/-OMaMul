package com.tkw.omamul.data.local

import com.tkw.omamul.data.CupDao
import com.tkw.omamul.data.model.Cup
import com.tkw.omamul.data.model.CupEntity
import com.tkw.omamul.data.model.CupEntityRequest
import com.tkw.omamul.data.model.CupListEntity
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.toRealmList
import io.realm.kotlin.mongodb.ext.insert
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import kotlin.reflect.KClass

class CupDaoImpl(r: Realm): CupDao {
    override val realm: Realm = r
    override val clazz: KClass<CupListEntity> = CupListEntity::class

    private val getCupList: MutableRealm.() -> CupListEntity? = {
        this.query(clazz, "cupId == $0", Cup.DEFAULT_CUP_ID).first().find()
    }

    override fun getCup(id: String): CupEntity? {
        return this.findFirst()?.cupList?.find { it.cupId == id }
    }

    override fun getCupListFlow(): Flow<ResultsChange<CupListEntity>> {
        return this.stream(this.findBy("cupId == $0", Cup.DEFAULT_CUP_ID))
    }

    override suspend fun createList() {
        this.upsert(CupListEntity())
    }

    override suspend fun insertCup(obj: CupEntityRequest) {
        realm.write {
            getCupList()?.cupList?.add(obj.toMapEntity())
        }
    }

    override suspend fun updateCup(cupId: String, target: CupEntityRequest) {
        realm.write {
            val origin = getCup(cupId)
            findLatest(origin!!)?.apply {
                cupName = target.cupName
                cupAmount = target.cupAmount
            }
        }
    }

    override suspend fun updateAll(list: List<CupEntityRequest>) {
        realm.write {
            getCupList()?.cupList?.clear()
            getCupList()?.cupList?.addAll(list.map { it.toMapEntity() })
        }
    }

    override suspend fun deleteCup(cupId: String) {
        realm.write {
            val cup = getCup(cupId)
            getCupList()?.cupList?.remove(cup)
        }
    }
}