package com.tkw.firebase

import android.app.Service
import android.content.Intent
import android.os.IBinder
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
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class BackupForeground: Service() {
    private val foregroundId = 1

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
        val notification = NotificationManager.notifyService(this, foregroundId)
        startForeground(foregroundId, notification)

        if(intent != null) {
            val isUpdate = intent.getBooleanExtra("isUpdate", false)

            googleDriveAuth.authorize {
                if (it.isSuccess) {
                    it.getOrNull()?.let { result ->
                        //백업 시작 브로드캐스트
                        if (isUpdate) {
                            doUpload(result.accessToken)
                        } else {
                            doBackUp(result.accessToken)
                        }
                    } ?: stopSelf()
                } else {
                    stopSelf()
                }
            }
        } else {
            stopSelf()
        }


        return START_NOT_STICKY
    }

    private fun doUpload(accessToken: String?) {
        val destRealmFile = File(applicationContext.filesDir, "default.realm")
//        val rotateAnim = syncRotateStart(dataBinding.settingInfo.ivSync)
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                googleDrive.upload(accessToken, destRealmFile, destRealmFile.name)
            }.onFailure {
                it.printStackTrace()
            }.onSuccess {
                prefDataRepository.saveLastSync(System.currentTimeMillis())
            }.also {
                //브로드캐스트 날리기
                stopSelf()
            }
        }
    }

    private fun doBackUp(accessToken: String?) {
        val sourceRealmFile = File(applicationContext.filesDir, "tmp.realm")
        val destRealmFile = File(applicationContext.filesDir, "default.realm")
//        val rotateAnim = syncRotateStart(dataBinding.settingInfo.ivSync)
        CoroutineScope(Dispatchers.IO).launch {
            runCatching {
                backUpRealm(accessToken, sourceRealmFile, destRealmFile)
            }.onFailure {
                it.printStackTrace()
            }.onSuccess {
                prefDataRepository.saveLastSync(System.currentTimeMillis())
            }.also {
                //브로드캐스트 날리기
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