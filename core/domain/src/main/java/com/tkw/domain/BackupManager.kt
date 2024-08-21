package com.tkw.domain

import java.io.File

interface BackupManager {
    fun upload(token: String?, file: File): String
    fun download(token: String?, destFile: File)
}