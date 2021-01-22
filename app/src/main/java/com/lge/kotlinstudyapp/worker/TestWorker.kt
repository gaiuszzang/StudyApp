package com.lge.kotlinstudyapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.lge.kotlinstudyapp.logd
import kotlinx.coroutines.delay

class TestWorker(appContext: Context, workerParams: WorkerParameters): CoroutineWorker(appContext, workerParams) {
    companion object {
        val TAG = "TestWorker"
    }
    override suspend fun doWork(): Result {
        val loopTime = inputData.getInt("LOOP_TIME", 10)
        logd(TAG, "Test Work Begin (loopTime = $loopTime)")
        for (i in 1..loopTime) {
            val progress = (i * 100)/loopTime
            setProgress(workDataOf("Progress" to progress))
            delay(1000)
            logd(TAG, "Test Work wait count($i)")
        }
        logd(TAG, "Test Work Finish")
        return Result.success()
    }
}