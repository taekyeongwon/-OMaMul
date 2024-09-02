package com.tkw.firebase

import android.content.Context

interface AccessTokenManager<T, U> {
    fun getAccessTokenAsync(
        context: Context,
        launcher: T?,
        block: (U) -> Unit
    )
}