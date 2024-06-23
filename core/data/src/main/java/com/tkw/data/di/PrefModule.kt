package com.tkw.data.di

import com.tkw.data.local.PrefDataRepositoryImpl
import com.tkw.datastore.PrefDataSource
import com.tkw.datastore.local.PrefLocalDataSourceImpl
import com.tkw.domain.PrefDataRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PrefModule {
    @Binds
    @Singleton
    abstract fun providePrefRepo(repo: PrefDataRepositoryImpl): PrefDataRepository

    @Binds
    @Singleton
    abstract fun providePrefDataSource(dataSource: PrefLocalDataSourceImpl): PrefDataSource
}