package com.tkw.domain

import java.io.File

interface BackupManager {
    fun upload(token: String?, file: File, backupFileName: String): String
    fun download(token: String?, destFile: File, backupFileName: String)
}