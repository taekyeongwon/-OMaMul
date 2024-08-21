package com.tkw.firebase

import android.content.Context
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.Scope
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.FileList
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.AccessToken
import com.google.auth.oauth2.GoogleCredentials
import com.tkw.domain.BackupManager
import com.tkw.domain.DriveAuthorize
import dagger.hilt.android.qualifiers.ActivityContext
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.util.Collections
import javax.inject.Inject

class GoogleDriveBackup @Inject constructor(
    @ActivityContext private val context: Context
): BackupManager, DriveAuthorize<ActivityResultLauncher<IntentSenderRequest>, AuthorizationResult> {
    private val requestedScopes = listOf(Scope(DriveScopes.DRIVE_FILE), Scope(DriveScopes.DRIVE_APPDATA))
    private val scope = listOf(DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_APPDATA)

    override fun upload(token: String?, file: java.io.File): String {
        val service = getDriveService(token)
        val mediaContent = FileContent("application/octet-stream", file)

        return try {
            val fileList = getList(token)
            val createdFile = if(fileList.isEmpty()) {
                // File's metadata.
                val fileMetadata = File()
                fileMetadata.setName("default.realm")
                fileMetadata.setParents(Collections.singletonList("appDataFolder"))

                service.files().create(fileMetadata, mediaContent)
                    .setFields("id") // response에서 받을 필드 정의
                    .execute()
            } else {
                service.files().update(fileList.files[0].id, null, mediaContent)
                    .setFields("id")
                    .execute()
            }

            println("File ID: " + createdFile.id)
            createdFile.id
        } catch (e: GoogleJsonResponseException) {
            // TODO(developer) - handle error appropriately
            System.err.println("Unable to create file: " + e.details)
            throw e
        }
    }

    override fun download(token: String?, destFile: java.io.File) {
        val service = getDriveService(token)
        val outputStream = ByteArrayOutputStream()
        try {
            val fileList = getList(token)
            if(fileList.isNotEmpty()) {
                service.files().get(fileList.files[0].id)
                    .executeMediaAndDownloadTo(outputStream)
                println("File ID: " + fileList.files[0].id)
            }

            outputStream.writeTo(FileOutputStream(destFile))
        } catch (e: GoogleJsonResponseException) {
            System.err.println("Unable to download file: " + e.details)
            throw e
        } finally {
            outputStream.close()
        }
    }

    override fun authorize(
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        resultListener: (Result<AuthorizationResult>) -> Unit
    ) {
        val authorizationRequest = AuthorizationRequest.builder().setRequestedScopes(requestedScopes).build()
        Identity.getAuthorizationClient(context)
            .authorize(authorizationRequest)
            .addOnSuccessListener {
                if(it.hasResolution()) {
                    val pendingIntent = it.pendingIntent
                    try {
                        val intent = IntentSenderRequest.Builder(pendingIntent!!.intentSender).build()
                        launcher.launch(intent)
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()
                    } catch (npe: NullPointerException) {
                        npe.printStackTrace()
                    }
                } else {
                    resultListener(Result.success(it))
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                resultListener(Result.failure(it))
            }
    }

    private fun getDriveService(token: String?): Drive {
        // Load pre-authorized user credentials from the environment.
        // TODO(developer) - See https://developers.google.com/identity for
        // guides on implementing OAuth2 for your application.
//        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
        val credentials = GoogleCredentials.create(AccessToken(token, null))
            .createScoped(scope)
        val requestInitializer: HttpRequestInitializer = HttpCredentialsAdapter(
            credentials
        )
//        Build a new authorized API client service.
        return Drive.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            requestInitializer
        )
            .setApplicationName("OMaMul App Drive")
            .build()
    }

    private fun getList(token: String?): FileList {
        val service = getDriveService(token)
        return try {
            val files = service.files().list()
                .setSpaces("appDataFolder")
                .setFields("files(id, name)")
                .execute()

            for(file in files.files) {
                System.out.printf("Found file: %s (%s)\n",
                    file.name, file.id)
            }
            files
        } catch (e: GoogleJsonResponseException) {
            System.err.println("Unable to list files: " + e.details)
            throw e
        }
    }
}