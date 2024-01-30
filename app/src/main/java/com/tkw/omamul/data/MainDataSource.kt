package com.tkw.omamul.data

interface MainDataSource {
    suspend fun getCountById(): Int
    suspend fun upsertCount()
}