package com.tkw.omamul

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.tkw.domain.DriveAuthorize
import com.tkw.domain.PrefDataRepository
import com.tkw.firebase.AccessTokenManager
import com.tkw.firebase.BackupForeground
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import java.util.Calendar

class ScheduledWorkManager(
    context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params), AccessTokenManager<Nothing,
        AuthorizationResult> by GoogleAccessTokenManager() {

    @InstallIn(SingletonComponent::class)
    @EntryPoint
    interface PrefRepositoryProvider {
        fun getPrefRepo(): PrefDataRepository
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
            Log.d("test", delayTime.toString())
            return delayTime
        }
    }

    override suspend fun doWork(): Result {
        try {
            Log.d("test", "work manager do work.")
            val hiltEntryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                PrefRepositoryProvider::class.java
            )
            val lastSyncMillis = hiltEntryPoint.getPrefRepo().fetchLastSync().first()

            getAccessTokenAsync(applicationContext, null) {
                if(it.accessToken != null && lastSyncMillis != -1L) {
                    startBackupService(true, it.accessToken)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }

        return Result.success()
    }

    private fun startBackupService(isUpdate: Boolean, accessToken: String?) {
        Intent(applicationContext, BackupForeground::class.java).apply {
            putExtra(BackupForeground.EXTRA_IS_UPDATE, isUpdate)
            putExtra(BackupForeground.EXTRA_ACCESS_TOKEN, accessToken)
            applicationContext.startForegroundService(this)
        }
    }

    private fun notify(context: Context) {
//        val data = inputData.getString()
//        NotificationManager.notify(context, RingTone.ALL.name, false)
    }

//    private fun doUpload(accessToken: String?) {
//        val destRealmFile = File(applicationContext.filesDir, "default.realm")
//        LocalBroadcastManager.getInstance(applicationContext)
//            .sendBroadcast(Intent(BackupForeground.ACTION_SERVICE_START))
//        CoroutineScope(Dispatchers.IO).launch {
//            runCatching {
//                googleDrive.upload(accessToken, destRealmFile, destRealmFile.name)
//            }.onFailure {
//                it.printStackTrace()
//            }.onSuccess {
//                prefDataRepository.saveLastSync(System.currentTimeMillis())
//            }.also {
//                LocalBroadcastManager.getInstance(applicationContext)
//                    .sendBroadcast(Intent(ACTION_SERVICE_STOP))
//                stopSelf()
//            }
//        }
//    }
}