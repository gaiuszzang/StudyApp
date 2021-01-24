package com.lge.kotlinstudyapp.server

import com.lge.kotlinstudyapp.server.data.DeviceLog
import com.lge.kotlinstudyapp.server.data.PostResult
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface AcanelServerRetrofitService {
    @POST("device/log")
    suspend fun putDeviceLog(@Body deviceLog: DeviceLog) : PostResult

    @Streaming
    @Multipart
    @POST("file")
    suspend fun postFile(@Part uploadFiles: List<MultipartBody.Part>): PostResult
}