package com.tkw.omamul.data.local

import com.tkw.omamul.data.model.CountEntity
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.types.RealmObject
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface CountDao {
    suspend fun query(): Int
    fun <T: RealmObject> queryStream(clazz: KClass<T>): Flow<ResultsChange<T>>?
    suspend fun addAsync()
    suspend fun removeAsync()
}