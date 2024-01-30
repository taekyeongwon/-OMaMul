package com.tkw.omamul.data

interface MainRepository {
    suspend fun getCount(): Int
    suspend fun addCount()
}