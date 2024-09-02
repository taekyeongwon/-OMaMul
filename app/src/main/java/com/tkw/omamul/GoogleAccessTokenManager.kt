package com.tkw.omamul

import android.content.Context
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.tkw.domain.DriveAuthorize
import com.tkw.firebase.AccessTokenManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * WorkManager 주기적 백업 용도로 사용하기 위한 AccessToken 반환용 클래스.
 * 구글 로그인용 launcher를 띄우지 않음.
 */
class GoogleAccessTokenManager:
    AccessTokenManager<Nothing, AuthorizationResult> {

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface GoogleDriveAuthProvider {
        fun getGoogleDriveAuth(): DriveAuthorize<AuthorizationResult>
    }

    override fun getAccessTokenAsync(
        context: Context,
        launcher: Nothing?,
        block: (AuthorizationResult) -> Unit
    ) {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context,
            GoogleDriveAuthProvider::class.java
        )
        hiltEntryPoint.getGoogleDriveAuth()
            .authorize {
                it.onSuccess { result ->
                    block(result)
                }
            }
    }
}