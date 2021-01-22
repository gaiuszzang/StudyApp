package com.lge.kotlinstudyapp.service

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.lge.kotlinstudyapp.logd
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class TestJobIntentService : JobIntentService() {
    companion object {
        private const val TAG = "TestJobIntentService"
        const val JOB_ID = 0
        const val TEST_ACTION = "ACTION_TEST"

        @JvmStatic
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, TestJobIntentService::class.java, JOB_ID, work)
        }
    }

    override fun onCreate() {
        super.onCreate()
        logd(TAG, "onCreate()")
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logd(TAG, "onStartCommand()")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        logd(TAG, "onDestroy()")
    }

    override fun onHandleWork(intent: Intent) {
        logd(TAG, "onHandleWork() : begin")
        val loopTime = intent.getIntExtra("LOOP_TIME", 2)
        runBlocking {
            logd(TAG, "onHandleWork() : runblocking begin")
            for (i in 1..loopTime) {
                delay(1000)
                logd(TAG, "onHandleWork() : Test Job wait count($i)")
            }
        }
        logd(TAG, "onHandleWork() : finish")
    }
}