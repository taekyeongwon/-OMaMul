package com.tkw.datastore.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.tkw.datastore.InitDataSource
import kotlinx.coroutines.flow.Flow

class InitLocalDataSourceImpl(dataStore: DataStore<Preferences>): InitDataSource {
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