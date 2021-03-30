package com.lge.kotlinstudyapp.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lge.kotlinstudyapp.logd
import com.lge.kotlinstudyapp.repo.Repo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LogWorker @AssistedInject constructor(@Assisted appContext: Context,
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
