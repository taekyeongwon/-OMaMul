package com.tkw.omamul.data.local

import com.tkw.omamul.data.CupDao
import com.tkw.omamul.data.CupRepository
import com.tkw.omamul.data.model.CupEntity
import com.tkw.omamul.data.model.CupEntityRequest
import com.tkw.omamul.data.model.CupListEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CupRepositoryImpl(private val cupDao: CupDao): CupRepository {
    override fun getCupById(id: String): CupEntity? = cupDao.getCup(id)

    override fun getCupList(): Flow<CupListEntity> {
        val cupFlow = cupDao.getCupListFlow()
        return flow {
            cupFlow.collect {
                val cupList = it.list.firstOrNull()
                if(cupList == null) {
                    createList()
                } else {
                    this.emit(cupList)
                }
            }
        }
    }

    override suspend fun createList() = cupDao.createList()

    override suspend fun insertCup(obj: CupEntityRequest) = cupDao.insertCup(obj)

    override suspend fun updateCup(cupId: String, target: CupEntityRequest) =
        cupDao.updateCup(cupId, target)

    override suspend fun updateAll(list: List<CupEntityRequest>) = cupDao.updateAll(list)

    override suspend fun deleteCup(cupId: String) = cupDao.deleteCup(cupId)
}