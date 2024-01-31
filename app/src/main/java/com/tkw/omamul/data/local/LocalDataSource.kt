package com.tkw.omamul.data.local

import com.tkw.omamul.data.MainDataSource
import com.tkw.omamul.data.model.CountEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

class LocalDataSource(private val countDao: CountDao): MainDataSource {
    override suspend fun getCountById(): Int {
        return countDao.query()
    }

    override fun getQueryByFlow(): Flow<CountEntity> {
        val countFlow = countDao.queryStream(CountEntity::class)
        return flow {
            countFlow?.collect {
                val count = it.list.lastOrNull() ?: CountEntity().apply { count = 0 }
                this.emit(count)
            }
        }
    }

    override suspend fun updateCount() {
        countDao.addAsync()
    }
}