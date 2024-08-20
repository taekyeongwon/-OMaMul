package com.tkw.firebase

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.tkw.domain.Authentication
import com.tkw.domain.BackupManager
import com.tkw.domain.DriveAuthorize
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.scopes.FragmentScoped

@Module
@InstallIn(FragmentComponent::class)
abstract class OAuthModule {
    @Binds
    @FragmentScoped
    abstract fun provideAuthenticator(
        authentication: GoogleOAuth
    ): Authentication

    @Binds
    @FragmentScoped
    abstract fun provideDrive(
        backupManager: GoogleDriveBackup
    ): BackupManager

    @Binds
    @FragmentScoped
    abstract fun provideAuthorize(
        backupManager: GoogleDriveBackup
    ): DriveAuthorize<ActivityResultLauncher<IntentSenderRequest>, AuthorizationResult>
}