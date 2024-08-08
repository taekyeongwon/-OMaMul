package com.tkw.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
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
        private val ALARM_WAKE_TIME_KEY = stringPreferencesKey("alarm_wake_time")
        private val ALARM_SLEEP_TIME_KEY = stringPreferencesKey("alarm_sleep_time")
        private val INTAKE_AMOUNT_KEY = intPreferencesKey("intake_amount")
        private val REACHED_GOAL_KEY = booleanPreferencesKey("reached_goal")
        private val INIT_FLAG_KEY = booleanPreferencesKey("init_flag")
        private val ALARM_ENABLED_KEY = booleanPreferencesKey("alarm_enabled")
    }
    private val defaultLang = "ko"
    private val defaultIntakeAmount = 2000
    private val defaultWakeUpTime = getFormattedTime(8, 0)
    private val defaultSleepTime = getFormattedTime(23, 0)
    private val defaultReachedGoal = false
    private val defaultInitFlag = false
    private val defaultAlarmEnabled = false

    override suspend fun saveLanguage(lang: String) {
        dataSource.saveData(LANG_KEY, lang)
    }

    override fun fetchLanguage(): Flow<String> = dataSource.fetchData(LANG_KEY, defaultLang)

    override suspend fun saveAlarmTime(wake: String, sleep: String) {
        dataSource.saveData(ALARM_WAKE_TIME_KEY, wake)
        dataSource.saveData(ALARM_SLEEP_TIME_KEY, sleep)
    }

    override fun fetchAlarmWakeTime(): Flow<String> = dataSource.fetchData(ALARM_WAKE_TIME_KEY, defaultWakeUpTime)

    override fun fetchAlarmSleepTime(): Flow<String> = dataSource.fetchData(ALARM_SLEEP_TIME_KEY, defaultSleepTime)

    override suspend fun saveIntakeAmount(amount: Int) {
        dataSource.saveData(INTAKE_AMOUNT_KEY, amount)
    }

    override fun fetchIntakeAmount(): Flow<Int> = dataSource.fetchData(INTAKE_AMOUNT_KEY, defaultIntakeAmount)

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

    private fun getFormattedTime(hour: Int, minute: Int): String {
        val formatter = DateTimeFormatter.ofPattern("a hh:mm")
        return LocalTime.of(hour, minute).format(formatter)
    }
}