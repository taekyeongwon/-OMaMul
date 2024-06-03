package com.tkw.common

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


/**
 * 권한 관련 설정 클래스
 * ActivityResultLauncher의 경우 create되기 전에 호출해야 함.
 */
object PermissionHelper {
    private var grantAction: () -> Unit = {}
    private var cancelAction: (Boolean) -> Unit = {}

    fun getPermissionResultCallback(
        context: Activity
    ) = ActivityResultCallback<Map<String, Boolean>> { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if(allGranted) {
            grantAction()
        } else {
            //shouldShowRequestPermissionRationale -> 직접 거부 누른 경우 true 반환.
            //권한 처음 보거나, 다시 묻지 않기, 허용한 경우엔 false 반환
            val neverAsk = permissions.entries.none {
                ActivityCompat.shouldShowRequestPermissionRationale(context, it.key)
            }
            cancelAction(neverAsk)
        }
    }

    fun requestPerms(
        context: Activity,
        requestCode: Int = 0,
        perms: Array<String>,
        permissionResultLauncher: ActivityResultLauncher<Array<String>>,
        grantAction: () -> Unit = {},
        cancelAction: (neverAsk: Boolean) -> Unit = {}
    ) {
        this.grantAction = grantAction
        this.cancelAction = cancelAction
        if (checkPerms(context, perms)) { //모두 허용되어 있으면 해당 동작 실행
            grantAction()
        } else {    //하나라도 거부되어 있으면(최초, 한번이라도 거절)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
                //api 30 이하인 경우 activity에서 onRequestPermissionsResult override 필요.
                ActivityCompat.requestPermissions(context, perms, requestCode)
            } else {
                permissionResultLauncher.launch(perms)
            }
        }
    }

    fun checkPerms(context: Activity, perms: Array<String>): Boolean {
        for (element in perms) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    element
                ) == PackageManager.PERMISSION_DENIED
            ) {
                return false
            }
        }
        return true
    }

    fun showSettingAlert(
        context: Context,
        title: String,
        message: String,
        positiveButtonTitle: String,
        negativeButtonTitle: String,
        positiveAction: () -> Unit
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveButtonTitle) { _, _ ->
                positiveAction()
            }
            .setNegativeButton(negativeButtonTitle) { _, _ ->

            }
            .show()
    }

    fun goToSetting(context: Activity, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.setData(Uri.parse("package:" + context.packageName))
        try {
            launcher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    fun goToNotificationSetting(context: Activity, launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        try {
            launcher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }
}