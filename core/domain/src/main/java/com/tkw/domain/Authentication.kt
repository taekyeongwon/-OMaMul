package com.tkw.domain

import com.tkw.domain.model.GoogleInfo

interface Authentication {
    fun fetchInfo(): GoogleInfo?
    fun signIn(result: (Boolean) -> Unit)
    fun signOut()
    fun isLoggedIn(): Boolean
}