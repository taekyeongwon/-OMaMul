package com.tkw.database

import java.io.File

interface FileManager {
    fun onRenameFile(file: File, renameFile: File)
    fun onDeleteFile(file: File)
}