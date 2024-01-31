package com.tkw.omamul.data

import com.tkw.omamul.data.model.CountEntity
import kotlinx.coroutines.flow.Flow

interface MainDataSource {
    suspend fun getCountById(): Int
    fun getQueryByFlow(): Flow<CountEntity>
    suspend fun updateCount()
}