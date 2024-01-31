package com.tkw.omamul.data

import com.tkw.omamul.data.model.CountEntity
import kotlinx.coroutines.flow.Flow

interface MainRepository {
    suspend fun getCount(): Int
    fun getCountByFlow(): Flow<CountEntity>
    suspend fun addCount()
}