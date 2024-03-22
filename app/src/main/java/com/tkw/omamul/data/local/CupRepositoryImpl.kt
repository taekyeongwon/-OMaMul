package com.tkw.omamul.data.local

import com.tkw.omamul.data.CupDao
import com.tkw.omamul.data.CupRepository
import com.tkw.omamul.data.model.CupEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CupRepositoryImpl(private val cupDao: CupDao): CupRepository {
    override fun getCupById(id: Int): CupEntity? = cupDao.getCup(id)

    override fun getCupList(): Flow<List<CupEntity>> {
        val cupFlow = cupDao.getCupListFlow()
        return flow {
            cupFlow.collect {
                this.emit(it.list.toList())
            }
        }
    }

    override suspend fun insertCup(obj: CupEntity) = cupDao.insertCup(obj)

    override suspend fun updateCup(obj: CupEntity) = cupDao.updateCup(obj)

    override suspend fun deleteCup(obj: CupEntity) = cupDao.deleteCup(obj)
}