package com.tkw.omamul.data

class MainRepositoryImpl(
    private val mainLocalDataSource: MainDataSource,
    private val mainRemoteDataSource: MainDataSource?
): MainRepository {
    override suspend fun getCount(): Int {
        return mainLocalDataSource.getCountById()
    }

    override suspend fun addCount() {
        mainLocalDataSource.upsertCount()
    }
}