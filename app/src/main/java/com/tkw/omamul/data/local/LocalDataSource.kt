package com.tkw.omamul.data.local

import com.tkw.omamul.data.MainDataSource

class LocalDataSource(private val countDao: CountDao): MainDataSource {
    override suspend fun getCountById(): Int {
        return countDao.query()
    }

    override suspend fun upsertCount() {
        countDao.addAsync()
    }
}