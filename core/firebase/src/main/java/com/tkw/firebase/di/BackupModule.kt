package com.tkw.firebase.di

import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.tkw.domain.BackupManager
import com.tkw.domain.DriveAuthorize
import com.tkw.firebase.GoogleDriveBackup
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BackupModule {
    @Binds
    @Singleton
    abstract fun provideDrive(
        backupManager: GoogleDriveBackup
    ): BackupManager

    @Binds
    @Singleton
    abstract fun provideAuthorize(
        backupManager: GoogleDriveBackup
    ): DriveAuthorize<AuthorizationResult>
}