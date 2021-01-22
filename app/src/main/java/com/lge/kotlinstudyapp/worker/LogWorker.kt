package com.lge.kotlinstudyapp.worker

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.lge.kotlinstudyapp.logd
import com.lge.kotlinstudyapp.repo.Repo
import kotlinx.coroutines.delay

class LogWorker @WorkerInject constructor(@Assisted appContext: Context,
                                          @Assisted workerParams: WorkerParameters,
                                          private val repo: Repo): CoroutineWorker(appContext, workerParams) {
    companion object {
        const val TAG = "LogWorker"
    }
    override suspend fun doWork(): Result {
        logd(TAG, "LogWorker Begin")
        repo.putDeviceLog()
        logd(TAG, "LogWorker Finish")
        return Result.success()
    }
}