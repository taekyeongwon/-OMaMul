package com.tkw.setting

import android.content.Context
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.tkw.domain.DriveAuthorize
import com.tkw.firebase.AccessTokenManager
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * 구글 드라이브 업로드를 위한 AccessToken 반환용 클래스.
 * 구글 로그인용 launcher를 띄움.
 */
class GoogleAccessTokenManager:
    AccessTokenManager<ActivityResultLauncher<IntentSenderRequest>, AuthorizationResult> {

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface GoogleDriveAuthProvider {
        fun getGoogleDriveAuth(): DriveAuthorize<AuthorizationResult>
    }

    override fun getAccessTokenAsync(
        context: Context,
        launcher: ActivityResultLauncher<IntentSenderRequest>?,
        block: (AuthorizationResult) -> Unit
    ) {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            context,
            GoogleDriveAuthProvider::class.java
        )
        hiltEntryPoint.getGoogleDriveAuth()
            .authorize {
                it.onSuccess { result ->
                    if (!googleAuthResultHasResolution(
                            launcher,
                            result
                        )
                    ) {
                        block(result)
                    }
                }
            }
    }

    private fun googleAuthResultHasResolution(
        launcher: ActivityResultLauncher<IntentSenderRequest>?,
        result: AuthorizationResult
    ): Boolean {
        return when {
            launcher == null -> false
            result.hasResolution() -> {
                val pendingIntent = result.pendingIntent
                try {
                    val intent = IntentSenderRequest.Builder(pendingIntent!!.intentSender).build()
                    launcher.launch(intent)
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                } catch (npe: NullPointerException) {
                    npe.printStackTrace()
                }
                true
            }
            else -> false
        }
    }
}