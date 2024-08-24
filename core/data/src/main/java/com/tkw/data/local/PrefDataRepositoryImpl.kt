package com.tkw.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tkw.datastore.PrefDataSource
import com.tkw.domain.PrefDataRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class PrefDataRepositoryImpl
@Inject constructor(private val dataSource: PrefDataSource): PrefDataRepository {
    companion object {
        private val LANG_KEY = stringPreferencesKey("language")
        private val REACHED_GOAL_KEY = booleanPreferencesKey("reached_goal")
        private val INIT_FLAG_KEY = booleanPreferencesKey("init_flag")
        private val ALARM_ENABLED_KEY = booleanPreferencesKey("alarm_enabled")
        private val LAST_SYNC_KEY = longPreferencesKey("last_sync")
    }
    private val defaultLang = "ko"
    private val defaultReachedGoal = false
    private val defaultInitFlag = false
    private val defaultAlarmEnabled = false
    private val defaultLastSync = -1L

    override suspend fun saveLanguage(lang: String) {
        dataSource.saveData(LANG_KEY, lang)
    }

    override fun fetchLanguage(): Flow<String> = dataSource.fetchData(LANG_KEY, defaultLang)

    override suspend fun saveReachedGoal(isReached: Boolean) {
        dataSource.saveData(REACHED_GOAL_KEY, isReached)
    }

    override fun fetchReachedGoal(): Flow<Boolean> = dataSource.fetchData(REACHED_GOAL_KEY, defaultReachedGoal)

    override suspend fun saveInitialFlag(flag: Boolean) {
        dataSource.saveData(INIT_FLAG_KEY, flag)
    }

    override fun fetchInitialFlag(): Flow<Boolean> = dataSource.fetchData(INIT_FLAG_KEY, defaultInitFlag)

    override suspend fun saveAlarmEnableFlag(flag: Boolean) {
        dataSource.saveData(ALARM_ENABLED_KEY, flag)
    }

    override fun fetchAlarmEnableFlag(): Flow<Boolean> = dataSource.fetchData(ALARM_ENABLED_KEY, defaultAlarmEnabled)

    override suspend fun saveLastSync(time: Long) {
        dataSource.saveData(LAST_SYNC_KEY, time)
    }

    override fun fetchLastSync(): Flow<Long> = dataSource.fetchData(LAST_SYNC_KEY, defaultLastSync)
}