package com.tkw.omamul.data

class MainRepositoryImpl(
    private val mainLocalDataSource: MainDataSource? = null,
    private val mainRemoteDataSource: MainDataSource? = null
): MainRepository {
}