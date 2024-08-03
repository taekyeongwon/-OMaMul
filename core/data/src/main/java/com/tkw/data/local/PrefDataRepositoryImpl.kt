package com.tkw.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tkw.datastore.PrefDataSource
import com.tkw.domain.PrefDataRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PrefDataRepositoryImpl
@Inject constructor(private val dataSource: PrefDataSource): PrefDataRepository {
    override suspend fun saveLanguage(lang: String) {
        dataSource.saveData(LANG_KEY, lang)
    }

    override fun fetchLanguage(): Flow<String?> = dataSource.fetchData(LANG_KEY)

    override suspend fun saveAlarmTime(wake: String, sleep: String) {
        dataSource.saveData(ALARM_WAKE_TIME_KEY, wake)
        dataSource.saveData(ALARM_SLEEP_TIME_KEY, sleep)
    }

    override fun fetchAlarmWakeTime(): Flow<String?> = dataSource.fetchData(ALARM_WAKE_TIME_KEY)

    override fun fetchAlarmSleepTime(): Flow<String?> = dataSource.fetchData(ALARM_SLEEP_TIME_KEY)

    override suspend fun saveIntakeAmount(amount: Int) {
        dataSource.saveData(INTAKE_AMOUNT_KEY, amount)
    }

    override fun fetchIntakeAmount(): Flow<Int?> = dataSource.fetchData(INTAKE_AMOUNT_KEY)

    override suspend fun saveReachedGoal(isReached: Boolean) {
        dataSource.saveData(REACHED_GOAL_KEY, isReached)
    }

    override fun fetchReachedGoal(): Flow<Boolean?> = dataSource.fetchData(REACHED_GOAL_KEY)

    override suspend fun saveInitialFlag(flag: Boolean) {
        dataSource.saveData(INIT_FLAG_KEY, flag)
    }

    override fun fetchInitialFlag(): Flow<Boolean?> = dataSource.fetchData(INIT_FLAG_KEY)

    override suspend fun saveAlarmEnableFlag(flag: Boolean) {
        dataSource.saveData(ALARM_ENABLED_KEY, flag)
    }

    override fun fetchAlarmEnableFlag(): Flow<Boolean?> = dataSource.fetchData(ALARM_ENABLED_KEY)

    companion object {
        val LANG_KEY = stringPreferencesKey("language")
        val ALARM_WAKE_TIME_KEY = stringPreferencesKey("alarm_wake_time")
        val ALARM_SLEEP_TIME_KEY = stringPreferencesKey("alarm_sleep_time")
        val INTAKE_AMOUNT_KEY = intPreferencesKey("intake_amount")
        val REACHED_GOAL_KEY = booleanPreferencesKey("reached_goal")
        val INIT_FLAG_KEY = booleanPreferencesKey("init_flag")
        val ALARM_ENABLED_KEY = booleanPreferencesKey("alarm_enabled")
    }
}