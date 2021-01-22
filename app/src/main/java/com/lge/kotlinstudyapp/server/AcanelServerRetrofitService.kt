package com.lge.kotlinstudyapp.server

import com.lge.kotlinstudyapp.server.data.DeviceLog
import com.lge.kotlinstudyapp.server.data.PostResult
import retrofit2.http.Body
import retrofit2.http.POST

interface AcanelServerRetrofitService {
    @POST("device/log")
    suspend fun putDeviceLog(@Body deviceLog: DeviceLog) : PostResult
}