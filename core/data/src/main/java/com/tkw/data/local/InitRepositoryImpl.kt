package com.tkw.data.local

import com.tkw.datastore.InitDataSource
import com.tkw.domain.InitRepository
import kotlinx.coroutines.flow.Flow

class InitRepositoryImpl(dataSource: InitDataSource): InitRepository {
    override fun saveLanguage(lang: String) {
        TODO("Not yet implemented")
    }

    override fun fetchLanguage(): Flow<String> {
        TODO("Not yet implemented")
    }

    override fun saveAlarmTime(wake: String, sleep: String) {
        TODO("Not yet implemented")
    }

    override fun fetchAlarmTime(): Flow<Pair<String, String>> {
        TODO("Not yet implemented")
    }

    override fun saveIntakeAmount(amount: Int) {
        TODO("Not yet implemented")
    }

    override fun fetchIntakeAmount(): Flow<Int> {
        TODO("Not yet implemented")
    }
}