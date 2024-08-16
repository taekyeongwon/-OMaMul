package com.tkw.domain

import kotlinx.coroutines.flow.Flow

interface PrefDataRepository {
    suspend fun saveLanguage(lang: String)
    fun fetchLanguage(): Flow<String>

    suspend fun saveIntakeAmount(amount: Int)
    fun fetchIntakeAmount(): Flow<Int>

    suspend fun saveReachedGoal(isReached: Boolean)
    fun fetchReachedGoal(): Flow<Boolean>

    suspend fun saveInitialFlag(flag: Boolean)
    fun fetchInitialFlag(): Flow<Boolean>

    suspend fun saveAlarmEnableFlag(flag: Boolean)
    fun fetchAlarmEnableFlag(): Flow<Boolean>

    // 0: ml, L
    // 1: fl.oz
    suspend fun saveUnit(unit: Int)
    fun fetchUnit(): Flow<Int>
}