package com.tkw.alarmnoti

import com.tkw.domain.IAlarmManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AlarmModule {
    @Binds
    @Singleton
    abstract fun provideAlarmManager(
        manager: WaterAlarmManager
    ): IAlarmManager

}