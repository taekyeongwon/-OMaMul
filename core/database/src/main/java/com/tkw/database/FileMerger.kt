package com.tkw.database

import java.io.File

interface FileMerger {
    suspend fun onMerge(sourceFile: File, destFile: File)
}