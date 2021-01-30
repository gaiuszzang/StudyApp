package com.lge.kotlinstudyapp.server

import com.lge.kotlinstudyapp.server.data.DeviceLog
import com.lge.kotlinstudyapp.server.data.ItemDto
import com.lge.kotlinstudyapp.server.data.PostResult
import com.lge.kotlinstudyapp.server.data.ProductDto
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface AcanelServer {
    @POST("device/log")
    suspend fun putDeviceLog(@Body deviceLog: DeviceLog) : PostResult

    @Multipart
    @POST("file")
    suspend fun postFile(@Part uploadFiles: List<MultipartBody.Part>): PostResult

    @GET("product/list")
    suspend fun getProductList() : List<ProductDto>

    @GET("item/list")
    suspend fun getItemList(@Query("pageId") pageId: Int) : List<ItemDto>
}