package com.tkw.domain

interface DriveAuthorize<T> {
    fun authorize(resultListener: (Result<T>) -> Unit)
}