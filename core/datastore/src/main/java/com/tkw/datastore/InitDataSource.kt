package com.tkw.datastore

import kotlinx.coroutines.flow.Flow

interface InitDataSource {
    fun saveLanguage(lang: String)
    fun fetchLanguage(): Flow<String>

    fun saveAlarmTime(wake: String, sleep: String)
    fun fetchAlarmTime(): Flow<Pair<String, String>>

    fun saveIntakeAmount(amount: Int)
    fun fetchIntakeAmount(): Flow<Int>
}