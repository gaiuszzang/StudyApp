package com.lge.kotlinstudyapp.activity

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.lge.kotlinstudyapp.KotlinStudyApplication
import com.lge.kotlinstudyapp.repo.Repo
import com.lge.kotlinstudyapp.worker.LogWorker
import com.lge.kotlinstudyapp.worker.TestWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(private val repo: Repo) : ViewModel() {
    val text = repo.getStoredText()

    val testWorkInfo = WorkManager
        .getInstance(KotlinStudyApplication.instance.applicationContext)
        .getWorkInfosForUniqueWorkLiveData(TestWorker.TAG)

    val testWorkProgress = Transformations.map(testWorkInfo) { workInfoList ->
        if (workInfoList.isNullOrEmpty()) return@map "0"
        return@map workInfoList.last().progress.getInt("Progress", 0).toString()
    }

    val logWorkInfo = WorkManager
        .getInstance(KotlinStudyApplication.instance.applicationContext)
        .getWorkInfosForUniqueWorkLiveData(LogWorker.TAG)

    val logWorkStatus = Transformations.map(logWorkInfo) { workInfoList ->
        if (workInfoList.isNullOrEmpty()) return@map "No Exist"
        return@map if (workInfoList.last().state == WorkInfo.State.RUNNING || workInfoList.last().state == WorkInfo.State.ENQUEUED) "Alive" else "No Alive"
    }

    suspend fun updateText(msg: String) {
        repo.setStoredText(msg)
    }

    suspend fun putDeviceLog() {
        repo.putDeviceLog()
    }
}