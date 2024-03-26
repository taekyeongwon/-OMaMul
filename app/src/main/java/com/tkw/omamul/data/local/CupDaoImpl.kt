package com.tkw.omamul.data.local

import com.tkw.omamul.data.CupDao
import com.tkw.omamul.data.model.Cup
import com.tkw.omamul.data.model.CupEntity
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
        this.query(clazz).first().find()
    }

    override fun getCup(id: String): CupEntity? {
        return this.findFirst()?.cupList?.find { it.cupId.toHexString() == id }
    }

    override fun getCupListFlow(): Flow<ResultsChange<CupListEntity>> {
        val test = this.findAll()
        val cupListId = this.findFirst()?.cupId ?: ""
        return this.stream(this.findBy("cupId == $0", cupListId))
    }

    override suspend fun createList() {
        this.upsert(CupListEntity())
    }

    override suspend fun insertCup(obj: CupEntity) {
        realm.write {
            getCupList()?.cupList?.add(obj)
        }
    }

    override suspend fun updateCup(origin: CupEntity, target: CupEntity) {
        realm.write {
            findLatest(origin)?.apply {
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

    override suspend fun deleteCup(obj: CupEntity) {
        realm.write {
            getCupList()?.cupList?.remove(obj)
        }
    }
}