package com.tkw.domain

import kotlinx.coroutines.flow.Flow

interface PrefDataRepository {
    suspend fun saveLanguage(lang: String)
    fun fetchLanguage(): Flow<String?>

    suspend fun saveAlarmTime(wake: String, sleep: String)
    fun fetchAlarmWakeTime(): Flow<String?>
    fun fetchAlarmSleepTime(): Flow<String?>

    suspend fun saveIntakeAmount(amount: Int)
    fun fetchIntakeAmount(): Flow<Int?>

    suspend fun saveInitialFlag(flag: Boolean)
    fun fetchInitialFlag(): Flow<Boolean?>

    suspend fun saveAlarmEnableFlag(flag: Boolean)
    fun fetchAlarmEnableFlag(): Flow<Boolean?>
}