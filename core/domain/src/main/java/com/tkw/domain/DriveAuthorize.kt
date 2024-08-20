package com.tkw.domain

interface DriveAuthorize<T, U> {
    fun authorize(launcher: T, resultListener: (Result<U>) -> Unit)
}