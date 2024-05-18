package com.tkw.datastore.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.tkw.datastore.PrefDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PrefLocalDataSourceImpl
@Inject constructor(private val dataStore: DataStore<Preferences>): PrefDataSource {
    override suspend fun <T> saveData(key: Preferences.Key<T>, value: T) {
        dataStore.edit {
            it[key] = value
        }
    }

    override fun <T> fetchData(key: Preferences.Key<T>): Flow<T?> =
        dataStore.data.map {//return type이 nullable이므로 Flow<T?>와 같이 nullable 타입으로 리턴
            it[key]
        }
}