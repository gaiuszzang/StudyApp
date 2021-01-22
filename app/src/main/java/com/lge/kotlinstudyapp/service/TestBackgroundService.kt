package com.lge.kotlinstudyapp.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.lge.kotlinstudyapp.logd
import java.lang.Thread.sleep

class TestBackgroundService: Service() {
    companion object {
        private const val TAG = "TestBackgroundService"
    }

    override fun onCreate() {
        logd(TAG, "onCreate")
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        logd(TAG, "onStartCommand")
        val thread = Thread {
            logd(TAG, "Thread Start")
            try {
                for(i in 1..100) {
                    logd(TAG, "I am in Thread $i")
                    sleep(1000)
                }
            } catch(e: Exception) {
                e.printStackTrace()
            }
            logd(TAG, "Thread Finish")
        }
        thread.start()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        logd(TAG, "onDestroy")
        super.onDestroy()
    }
}