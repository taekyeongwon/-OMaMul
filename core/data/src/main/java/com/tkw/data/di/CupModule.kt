package com.tkw.data.di

import com.tkw.data.local.CupRepositoryImpl
import com.tkw.data.local.WaterRepositoryImpl
import com.tkw.database.CupDao
import com.tkw.database.WaterDao
import com.tkw.database.local.CupDaoImpl
import com.tkw.database.local.WaterDaoImpl
import com.tkw.domain.CupRepository
import com.tkw.domain.WaterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CupModule {
    @Binds
    @Singleton
    abstract fun provideCupDao(dao: CupDaoImpl): CupDao

    @Binds
    @Singleton
    abstract fun provideWaterDao(dao: WaterDaoImpl): WaterDao

    @Binds
    @Singleton
    abstract fun provideCupRepo(repo: CupRepositoryImpl): CupRepository

    @Binds
    @Singleton
    abstract fun provideWaterRepo(repo: WaterRepositoryImpl): WaterRepository
}