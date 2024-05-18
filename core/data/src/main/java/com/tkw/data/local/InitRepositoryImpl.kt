package com.tkw.data.local

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.tkw.datastore.PrefDataSource
import com.tkw.domain.InitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InitRepositoryImpl
@Inject constructor(private val dataSource: PrefDataSource): InitRepository {
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

    companion object {
        val LANG_KEY = stringPreferencesKey("language")
        val ALARM_WAKE_TIME_KEY = stringPreferencesKey("alarm_wake_time")
        val ALARM_SLEEP_TIME_KEY = stringPreferencesKey("alarm_sleep_time")
        val INTAKE_AMOUNT_KEY = intPreferencesKey("intake_amount")
    }
}