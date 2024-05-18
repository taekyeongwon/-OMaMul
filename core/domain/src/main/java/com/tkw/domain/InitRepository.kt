package com.tkw.domain

import kotlinx.coroutines.flow.Flow

interface InitRepository {
    suspend fun saveLanguage(lang: String)
    fun fetchLanguage(): Flow<String?>

    suspend fun saveAlarmTime(wake: String, sleep: String)
    fun fetchAlarmWakeTime(): Flow<String?>
    fun fetchAlarmSleepTime(): Flow<String?>

    suspend fun saveIntakeAmount(amount: Int)
    fun fetchIntakeAmount(): Flow<Int?>
}