package com.tkw.omamul.data.local

import com.tkw.omamul.data.model.CountEntity
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow

interface CountDao {
    suspend fun query(): Int
    suspend fun queryStream(): Flow<ResultsChange<CountEntity>>?
    suspend fun addAsync()
    suspend fun removeAsync()
}