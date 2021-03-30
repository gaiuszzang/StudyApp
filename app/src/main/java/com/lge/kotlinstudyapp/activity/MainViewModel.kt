package com.lge.kotlinstudyapp.activity

import android.os.Environment
import androidx.lifecycle.*
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.lge.kotlinstudyapp.KotlinStudyApplication
import com.lge.kotlinstudyapp.logd
import com.lge.kotlinstudyapp.repo.Repo
import com.lge.kotlinstudyapp.worker.LogWorker
import com.lge.kotlinstudyapp.worker.TestWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repo: Repo) : ViewModel() {
    companion object {
        private const val TAG = "MainViewModel"
    }
    val text = repo.getStoredText().asLiveData()

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

    val uploadProgress = MutableLiveData<String>()

    suspend fun updateText(msg: String) {
        repo.setStoredText(msg)
    }

    suspend fun putDeviceLog() {
        repo.putDeviceLog()
    }

    fun uploadFile() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.uploadFile(File(Environment.getExternalStorageDirectory().absolutePath + "/Preload/LG/Life_Is_Good(Piano).flac")) { upSize: Long, fileSize: Long ->
                logd(TAG, "File Upload : $upSize / $fileSize")
                val progress = (upSize * 100 / fileSize).toInt()
                uploadProgress.postValue(progress.toString())
            }
        }
    }

    fun cancelUpload() {
        viewModelScope.launch {
            repo.cancelUpload()
            uploadProgress.postValue("canceled")
        }
    }
}
