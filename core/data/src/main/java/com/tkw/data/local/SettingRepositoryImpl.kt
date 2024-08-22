package com.tkw.data.local

import com.tkw.database.FileMerger
import com.tkw.domain.SettingRepository
import java.io.File
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val merger: FileMerger
): SettingRepository {
    override suspend fun merge(sourceFile: File, destFile: File) {
        merger.onMerge(sourceFile, destFile)
    }
}