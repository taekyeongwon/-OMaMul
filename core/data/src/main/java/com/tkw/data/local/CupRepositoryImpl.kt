package com.tkw.data.local

import com.tkw.database.CupDao
import com.tkw.database.model.CupEntity
import com.tkw.database.model.CupListEntity
import com.tkw.domain.CupRepository
import com.tkw.domain.model.Cup
import com.tkw.domain.model.CupList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CupRepositoryImpl(private val cupDao: CupDao): CupRepository {
    override fun getCupById(id: String): Cup? = cupDao.getCup(id)?.let {
        CupMapper.cupToModel(it)
    }

    override fun getCupList(): Flow<CupList> {
        val cupFlow = cupDao.getCupListFlow()
        return flow {
            cupFlow.collect {
                val cupList = it.list.firstOrNull()
                if(cupList == null) {
                    createList()
                } else {
                    this.emit(CupMapper.cupListToModel(cupList))
                }
            }
        }
    }

    override suspend fun createList() = cupDao.createList()

    override suspend fun insertCup(cupName: String, cupAmount: Int) {
        val cup = CupEntity().apply {
            this.cupName = cupName
            this.cupAmount = cupAmount
        }
        cupDao.insertCup(cup)
    }

    override suspend fun updateCup(cupId: String, cupName: String, cupAmount: Int) {
        val target = CupEntity().apply {
            this.cupId = cupId
            this.cupName = cupName
            this.cupAmount = cupAmount
        }
        cupDao.updateCup(target)
    }

    override suspend fun updateAll(list: List<Cup>) {
        val mappedList = list.map {
            CupMapper.cupToEntity(it)
        }
        cupDao.updateAll(mappedList)
    }

    override suspend fun deleteCup(cupId: String) = cupDao.deleteCup(cupId)
}