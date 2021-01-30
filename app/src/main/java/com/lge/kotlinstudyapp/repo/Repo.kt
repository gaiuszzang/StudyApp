package com.lge.kotlinstudyapp.repo

import android.os.Build
import android.os.Environment
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.lge.kotlinstudyapp.KotlinStudyApplication
import com.lge.kotlinstudyapp.db.AdvertiseDto
import com.lge.kotlinstudyapp.db.KeyValueData
import com.lge.kotlinstudyapp.db.Music
import com.lge.kotlinstudyapp.server.data.ProductDto
import com.lge.kotlinstudyapp.logd
import com.lge.kotlinstudyapp.logw
import com.lge.kotlinstudyapp.server.AcanelServerRetrofitService
import com.lge.kotlinstudyapp.server.ProgressRequestBody
import com.lge.kotlinstudyapp.server.data.DeviceLog
import com.lge.kotlinstudyapp.server.data.ItemDto
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType
import okhttp3.MultipartBody
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repo @Inject constructor() {
    companion object {
        private const val TAG = "Repo"
        private const val STORED_TEXT_KEY = "StoredText"
    }

    private val keyValueDao = KotlinStudyApplication.instance.db.keyValueDao()
    private val networkSource = AcanelServerRetrofitService.service
    private val itemPagingSource = ItemPagingSource

    fun getStoredText(): Flow<String?> {
        return keyValueDao.getData(STORED_TEXT_KEY)
    }

    suspend fun setStoredText(text: String) = withContext(Dispatchers.IO) {
        keyValueDao.putData(KeyValueData(STORED_TEXT_KEY, text))
    }

    suspend fun putDeviceLog() = withContext(Dispatchers.IO) {
        val datetime = Date().toString()
        val modelName = Build.MODEL
        val devStatus = "Status good."
        logd(TAG, "putDeviceLog() : {$datetime,  $modelName, $devStatus}")
        val deviceLog = DeviceLog(datetime, modelName, devStatus)
        try {
            val result = networkSource.putDeviceLog(deviceLog)
            logd(TAG, "result = ${result.result}")
        } catch (e: Exception) {
            logw(TAG, "result = Fail")
        }
    }

    private var uploadJob : Job? = null
    suspend fun uploadFile(file: File, listener: ((upsize: Long, totalsize: Long) -> Unit)?) = withContext(Dispatchers.Main) {
        val fileList = mutableListOf<MultipartBody.Part>()
        val fileBody = ProgressRequestBody(MediaType.parse("text/plain")!!, file, listener)
        fileList.add(MultipartBody.Part.createFormData("files", file.name, fileBody))
        uploadJob = launch(Dispatchers.IO) {
            try {
                val result = networkSource.postFile(fileList)
                logd(TAG, "result = ${result.result}")
            } catch (e: Exception) {
                logw(TAG, "result = Fail")
                e.printStackTrace()
            }
            uploadJob = null
        }
    }
    suspend fun cancelUpload() = withContext(Dispatchers.Main) {
        uploadJob?.cancelAndJoin()
        uploadJob = null
    }



    suspend fun getAdvertise1List() : List<AdvertiseDto> {
        delay(100)
        return arrayListOf<AdvertiseDto>(
            AdvertiseDto(1, "Advertise 1", "http://adv01"),
            AdvertiseDto(2, "Advertise 2", "http://adv02"),
            AdvertiseDto(3, "Advertise 3", "http://adv02")
        )
    }
    suspend fun getAdvertise2List() : List<AdvertiseDto> {
        delay(100)
        return arrayListOf<AdvertiseDto>(
            AdvertiseDto(4, "Advertise 4", "http://adv04"),
            AdvertiseDto(5, "Advertise 5", "http://adv05"),
            AdvertiseDto(6, "Advertise 6", "http://adv06"),
            AdvertiseDto(7, "Advertise 7", "http://adv07")
        )
    }

    suspend fun getMainProduct1List() : List<ProductDto> {
        delay(100)
        return arrayListOf<ProductDto>(
            ProductDto(1, null, "Product 1", 1000, 0),
            ProductDto(2, null, "Product 2", 2000, 0),
            ProductDto(3, null, "Product 3", 3000, 0),
            ProductDto(4, null, "Product 4", 4000, 0),
            ProductDto(5, null, "Product 5", 5000, 0),
            ProductDto(6, null, "Product 6", 6000, 0)
        )
    }

    suspend fun getMainProduct2List() =  networkSource.getProductList()

    fun getItemListFlow() : Flow<PagingData<ItemDto>> {
        return Pager(PagingConfig(pageSize = 20)) { itemPagingSource }.flow
    }

    suspend fun getMusicList() : List<Music> {
        val extPath = Environment.getExternalStorageDirectory().absolutePath
        return arrayListOf(
            Music( "life_is_good_piano","$extPath/Preload/LG/Life_Is_Good(Piano).flac", "Life Is Good Piano", "LGE Artist", "Genre 1"),
            Music("life_is_good_strquartet", "$extPath/Preload/LG/Life_Is_Good(String_Quartet).flac", "Life Is Good String Quartet", "LG Artist", "Genre 2"),
            Music("life_is_good_vivid_pop", "$extPath/Preload/LG/Life_Is_Good(Vivid_Pop).flac", "Life Is Good Vivid Pop", "LG Artist", "Genre 3"),
            Music("the_coldest_shoulder", "$extPath/Preload/LG/the_coldest_shoulder.mp3", "The Coldest Shoulder", "Youtube", "Genre X")
        )
    }
}