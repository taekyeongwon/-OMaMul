package com.tkw.omamul.data

import com.tkw.omamul.data.model.CountEntity
import kotlinx.coroutines.flow.Flow

class MainRepositoryImpl(
    private val mainLocalDataSource: MainDataSource,
    private val mainRemoteDataSource: MainDataSource?
): MainRepository {
    override suspend fun getCount(): Int {
        return mainLocalDataSource.getCountById()
    }

    override fun getCountByFlow(): Flow<CountEntity> {
        return mainLocalDataSource.getQueryByFlow()
    }

    override suspend fun addCount() {
        mainLocalDataSource.updateCount()
    }

    override suspend fun removeCount(obj: CountEntity) {
        mainLocalDataSource.deleteCount(obj)
    }
}