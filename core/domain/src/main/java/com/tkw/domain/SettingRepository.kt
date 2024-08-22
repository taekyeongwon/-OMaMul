package com.tkw.domain

import java.io.File

interface SettingRepository {
    suspend fun merge(sourceFile: File, destFile: File)
}