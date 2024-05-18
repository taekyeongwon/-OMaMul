package com.tkw.datastore

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface PrefDataSource {
    suspend fun <T> saveData(key: Preferences.Key<T>, value: T)
    fun <T> fetchData(key: Preferences.Key<T>): Flow<T?>
}