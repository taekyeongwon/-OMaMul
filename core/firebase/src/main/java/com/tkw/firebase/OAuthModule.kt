package com.tkw.firebase

import com.tkw.domain.Authentication
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

}