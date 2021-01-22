package com.lge.kotlinstudyapp

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.room.Room
import androidx.work.Configuration
import com.lge.kotlinstudyapp.db.AppDatabase
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class KotlinStudyApplication : Application(), Configuration.Provider {
    companion object {
        lateinit var instance: KotlinStudyApplication
    }
    val db by lazy {
        Room.databaseBuilder(applicationContext, AppDatabase::class.java, "kotlinStudy.db").build()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    //for Worker Hilt DI
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() = Configuration.Builder()
        .setWorkerFactory(workerFactory)
        .build()
}
private const val TAG = "KotlinStudyApp"
fun logd(tag: String, msg: String) = Log.d(TAG, "[$tag] $msg")
fun logi(tag: String, msg: String) = Log.i(TAG, "[$tag] $msg")
fun logw(tag: String, msg: String) = Log.w(TAG, "[$tag] $msg")
fun loge(tag: String, msg: String) = Log.e(TAG, "[$tag] $msg")