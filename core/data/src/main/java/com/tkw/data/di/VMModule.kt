package com.tkw.data.di

import com.tkw.data.local.CupRepositoryImpl
import com.tkw.data.local.PrefDataRepositoryImpl
import com.tkw.data.local.WaterRepositoryImpl
import com.tkw.database.CupDao
import com.tkw.database.WaterDao
import com.tkw.database.local.CupDaoImpl
import com.tkw.database.local.WaterDaoImpl
import com.tkw.datastore.PrefDataSource
import com.tkw.datastore.local.PrefLocalDataSourceImpl
import com.tkw.domain.CupRepository
import com.tkw.domain.PrefDataRepository
import com.tkw.domain.WaterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class VMModule {
    @Binds
    @ViewModelScoped
    abstract fun provideCupRepo(repo: CupRepositoryImpl): CupRepository

    @Binds
    @ViewModelScoped
    abstract fun provideWaterRepo(repo: WaterRepositoryImpl): WaterRepository

    @Binds
    @ViewModelScoped
    abstract fun provideInitRepo(repo: PrefDataRepositoryImpl): PrefDataRepository

    @Binds
    @ViewModelScoped
    abstract fun provideCupDao(dao: CupDaoImpl): CupDao

    @Binds
    @ViewModelScoped
    abstract fun provideWaterDao(dao: WaterDaoImpl): WaterDao

    @Binds
    @ViewModelScoped
    abstract fun provideInitDataSource(dataSource: PrefLocalDataSourceImpl): PrefDataSource

}