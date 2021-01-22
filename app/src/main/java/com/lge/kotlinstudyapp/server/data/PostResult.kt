package com.lge.kotlinstudyapp.server.data

import com.google.gson.annotations.SerializedName

data class PostResult(
    @SerializedName("result")
    val result: String
)
