package com.tkw.alarmnoti

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.tkw.domain.model.RingTone
import java.util.concurrent.TimeUnit

class ScheduledWorkManager(
    context: Context,
    params: WorkerParameters
): CoroutineWorker(context, params) {

    companion object{
        const val WORK_NAME = "Notification Work"

        fun getCertainTime(): Long {
            return 1000 * 60
        }
    }

    override suspend fun doWork(): Result {
        try {
            Log.d("test", "work manager do work.")
            notify(applicationContext)

            val oneTimeWorkRequest = OneTimeWorkRequestBuilder<ScheduledWorkManager>()
                .setInitialDelay(getCertainTime(), TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                oneTimeWorkRequest
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }

        return Result.success()
    }

    private fun notify(context: Context) {
//        val data = inputData.getString()
//        NotificationManager.notify(context, RingTone.ALL.name, false)
    }
}