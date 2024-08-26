package com.tkw.firebase

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.tkw.alarmnoti.NotificationManager
import com.tkw.domain.BackupManager
import com.tkw.domain.DriveAuthorize
import com.tkw.domain.PrefDataRepository
import com.tkw.domain.SettingRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class BackupForeground: Service() {
    companion object {
        private const val FOREGROUND_ID = 1
        const val EXTRA_IS_UPDATE = "isUpdate"
        const val EXTRA_ACCESS_TOKEN = "accessToken"
        const val ACTION_SERVICE_START = "action_service_start"
        const val ACTION_SERVICE_STOP = "action_service_stop"
    }

    @Inject
    lateinit var googleDrive: BackupManager

    @Inject
    lateinit var googleDriveAuth: DriveAuthorize<AuthorizationResult>

    @Inject
    lateinit var prefDataRepository: PrefDataRepository

    @Inject
    lateinit var settingRepository: SettingRepository

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationManager.notifyService(this, FOREGROUND_ID)
        startForeground(FOREGROUND_ID, notification)

        if(intent != null) {
            val isUpdate = intent.getBooleanExtra(EXTRA_IS_UPDATE, false)
            val accessToken = intent.getStringExtra(EXTRA_ACCESS_TOKEN)

            if (isUpdate) {
                doUpload(accessToken)
            } else {
                doBackUp(accessToken)
            }
        } else {
            stopSelf()
        }


        return START_NOT_STICKY
    }

    private fun doUpload(accessToken: String?) {
        val destRealmFile = File(applicationContext.filesDir, "default.realm")
        LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(Intent(ACTION_SERVICE_START))
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                googleDrive.upload(accessToken, destRealmFile, destRealmFile.name)
            }.onFailure {
                it.printStackTrace()
            }.onSuccess {
                prefDataRepository.saveLastSync(System.currentTimeMillis())
            }.also {
                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(Intent(ACTION_SERVICE_STOP))
                stopSelf()
            }
        }
    }

    private fun doBackUp(accessToken: String?) {
        val sourceRealmFile = File(applicationContext.filesDir, "tmp.realm")
        val destRealmFile = File(applicationContext.filesDir, "default.realm")
        LocalBroadcastManager.getInstance(applicationContext)
            .sendBroadcast(Intent(ACTION_SERVICE_START))
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                backUpRealm(accessToken, sourceRealmFile, destRealmFile)
            }.onFailure {
                it.printStackTrace()
            }.onSuccess {
                prefDataRepository.saveLastSync(System.currentTimeMillis())
            }.also {
                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(Intent(ACTION_SERVICE_STOP))
                stopSelf()
            }
        }
    }

    private suspend fun backUpRealm(accessToken: String?, sourceFile: File, destFile: File) {
        val backUpFileName = destFile.name
        googleDrive.download(accessToken, sourceFile, backUpFileName)
        settingRepository.merge(sourceFile, destFile)
        googleDrive.upload(accessToken, destFile, backUpFileName)
    }
}