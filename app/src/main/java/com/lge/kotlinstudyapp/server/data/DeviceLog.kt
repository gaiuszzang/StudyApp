package com.lge.kotlinstudyapp.server.data

import com.google.gson.annotations.SerializedName

data class DeviceLog(
    @SerializedName("datetime")
    val datetime: String,
    @SerializedName("modelName")
    val modelName: String,
    @SerializedName("devStatus")
    val devStatus: String)