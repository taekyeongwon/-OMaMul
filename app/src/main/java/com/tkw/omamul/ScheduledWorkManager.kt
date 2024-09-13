package com.tkw.omamul

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.tkw.alarmnoti.NotificationManager
import com.tkw.domain.BackupManager
import com.tkw.domain.PrefDataRepository
import com.tkw.firebase.AccessTokenManager
import com.tkw.firebase.BackupForeground
import com.tkw.firebase.BackupForeground.Companion.ACTION_SERVICE_STOP
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

class ScheduledWorkManager(
    context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params), AccessTokenManager<Nothing,
        AuthorizationResult> by GoogleAccessTokenManager() {
    override suspend fun doWork(): Result {
        try {
            Log.d("WorkManager", "work manager do work.")
            val hiltEntryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                DependencyProvider::class.java
            )
            val lastSyncMillis = hiltEntryPoint.getPrefRepo().fetchLastSync().first()

            getAccessTokenAsync(applicationContext, null) {
                if(it.accessToken != null && lastSyncMillis != -1L) {
                    doUpload(it.accessToken)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }

        return Result.success()
    }

    private fun doUpload(accessToken: String?) {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            DependencyProvider::class.java
        )
        val destRealmFile = File(applicationContext.filesDir, BackupForeground.BACKUP_FILE_NAME)

        notifyUpload()
        LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(Intent(BackupForeground.ACTION_SERVICE_START))
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                hiltEntryPoint.getGoogleDrive()
                    .upload(accessToken, destRealmFile, destRealmFile.name)
            }.onFailure {
                it.printStackTrace()
            }.onSuccess {
                hiltEntryPoint.getPrefRepo().saveLastSync(System.currentTimeMillis())
            }.also {
                cancelNotify()
                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(Intent(ACTION_SERVICE_STOP))
            }
        }
    }

    private fun notifyUpload() {
        NotificationManager.notifyService(applicationContext, BackupForeground.FOREGROUND_ID)
    }

    private fun cancelNotify() {
        NotificationManager.cancelNotify(applicationContext, BackupForeground.FOREGROUND_ID)
    }

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface DependencyProvider {
        fun getPrefRepo(): PrefDataRepository
        fun getGoogleDrive(): BackupManager
    }

    companion object{
        const val WORK_NAME = "Backup"

        fun getRemainTime(): Long {
            val backupTime = Calendar.getInstance()
            backupTime.set(Calendar.HOUR_OF_DAY, 3)
            backupTime.set(Calendar.MINUTE, 0)
            backupTime.set(Calendar.SECOND, 0)
            if(backupTime.before(Calendar.getInstance())) {
                backupTime.add(Calendar.DAY_OF_MONTH, 1)
            }
            val delayTime = backupTime.timeInMillis - System.currentTimeMillis()
            return delayTime
        }
    }
}